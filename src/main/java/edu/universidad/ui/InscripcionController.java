package edu.universidad.ui; // Controlador de la pestaña Inscripciones

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.universidad.persistence.H2DB; // conexión H2

/**
 * Reglas de negocio reforzadas:
 *  - El semestre de la inscripción SIEMPRE se toma del curso (ignora el del formulario).
 *  - Solo permite inscribir/actualizar si el curso está ACTIVO.
 *  - El semestre del estudiante debe coincidir con el del curso.
 */
public class InscripcionController {

    // ===== Formulario =====
    @FXML private TextField txtCursoId;
    @FXML private TextField txtCodigoEst;
    @FXML private TextField txtAnio;
    @FXML private TextField txtSemestre; // informativo

    // ===== Tabla =====
    @FXML private TableView<Row> tabla;
    @FXML private TableColumn<Row, Number> colCurso;
    @FXML private TableColumn<Row, Number> colCodigo;
    @FXML private TableColumn<Row, Number> colAnio;
    @FXML private TableColumn<Row, Number> colSemestre;

    @FXML private Label lblEstado;

    private final ObservableList<Row> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colCurso.setCellValueFactory(c -> c.getValue().cursoIdProperty());
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colAnio.setCellValueFactory(c -> c.getValue().anioProperty());
        colSemestre.setCellValueFactory(c -> c.getValue().semestreProperty());
        tabla.setItems(datos);

        if (txtSemestre != null) txtSemestre.setEditable(false);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtCursoId.setText(Integer.toString(sel.getCursoId()));
                txtCodigoEst.setText(Double.toString(sel.getCodigo()));
                txtAnio.setText(Integer.toString(sel.getAnio()));
                txtSemestre.setText(Integer.toString(sel.getSemestre()));
            }
        });

        recargar(null);
    }

    // ===== Acciones =====
    @FXML
    private void onCrear(ActionEvent e) {
        Row r = leer(); if (r == null) return;

        Integer sc = getSemestreCurso(r.getCursoId());
        if (sc == null) { error("El curso " + r.getCursoId() + " no existe."); return; }

        Boolean activo = isCursoActivo(r.getCursoId());
        if (activo == null || !activo) { error("El curso " + r.getCursoId() + " no está activo. No es posible inscribir."); return; }

        Integer se = getSemestreEst(r.getCodigo());
        if (se == null) { error("El estudiante " + r.getCodigo() + " no existe."); return; }

        if (!sc.equals(se)) {
            error("Semestre distinto: curso=" + sc + " vs estudiante=" + se);
            return;
        }

        final String sql = "MERGE INTO INSCRIPCION (CURSO_ID,ESTUDIANTE_CODIGO,ANIO,SEMESTRE) KEY(CURSO_ID,ESTUDIANTE_CODIGO) VALUES (?,?,?,?)";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getCursoId());
            ps.setDouble(2, r.getCodigo());
            ps.setInt(3, r.getAnio());
            ps.setInt(4, sc); // semestre del CURSO
            ps.executeUpdate();
            info("Inscripción guardada.");
            recargar(null);
            onLimpiar(null);
        } catch (SQLException ex) {
            error("Error guardando inscripción: " + ex.getMessage());
        }
    }

    @FXML
    private void onActualizar(ActionEvent e) {
        Row r = leer(); if (r == null) return;

        Integer sc = getSemestreCurso(r.getCursoId());
        if (sc == null) { error("El curso " + r.getCursoId() + " no existe."); return; }

        Boolean activo = isCursoActivo(r.getCursoId());
        if (activo == null || !activo) { error("El curso " + r.getCursoId() + " no está activo. No es posible actualizar inscripciones."); return; }

        Integer se = getSemestreEst(r.getCodigo());
        if (se == null) { error("El estudiante " + r.getCodigo() + " no existe."); return; }

        if (!sc.equals(se)) {
            error("Semestre distinto: curso=" + sc + " vs estudiante=" + se);
            return;
        }

        final String sql = "UPDATE INSCRIPCION SET ANIO=?, SEMESTRE=? WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getAnio());
            ps.setInt(2, sc);                // semestre del CURSO
            ps.setInt(3, r.getCursoId());
            ps.setDouble(4, r.getCodigo());
            int n = ps.executeUpdate();
            if (n == 0) info("No se encontró la inscripción a actualizar.");
            else info("Inscripción actualizada.");
            recargar(null);
            onLimpiar(null);
        } catch (SQLException ex) {
            error("Error actualizando inscripción: " + ex.getMessage());
        }
    }

    @FXML
    private void onEliminar(ActionEvent e) {
        Row sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Selecciona una fila para desinscribir."); return; }

        final String sql = "DELETE FROM INSCRIPCION WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sel.getCursoId());
            ps.setDouble(2, sel.getCodigo());
            int n = ps.executeUpdate();
            if (n == 0) info("No se eliminó ninguna inscripción.");
            else info("Inscripción eliminada.");
            recargar(null);
            onLimpiar(null);
        } catch (SQLException ex) {
            error("Error desinscribiendo: " + ex.getMessage());
        }
    }

    @FXML private void onRecargar(ActionEvent e) { recargar(null); }

    @FXML
    private void onLimpiar(ActionEvent e) {
        txtCursoId.clear();
        txtCodigoEst.clear();
        txtAnio.clear();
        txtSemestre.clear();
        tabla.getSelectionModel().clearSelection();
    }

    // ===== Listado =====
    private void recargar(ActionEvent e) {
        datos.clear();
        final String sql = "SELECT CURSO_ID,ESTUDIANTE_CODIGO,ANIO,SEMESTRE FROM INSCRIPCION ORDER BY CURSO_ID,ESTUDIANTE_CODIGO";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.add(new Row(rs.getInt(1), rs.getDouble(2), rs.getInt(3), rs.getInt(4)));
            estado("Datos recargados");
        } catch (SQLException ex) {
            error("Error listando inscripciones: " + ex.getMessage());
        }
    }

    // ===== Lectura/validación de formulario =====
    private Row leer() {
        int curso; double codigo; int anio; int semestre;
        try { curso = Integer.parseInt(txtCursoId.getText().trim()); }
        catch (Exception ex) { error("El campo 'Curso ID' debe ser entero."); return null; }

        try { codigo = Double.parseDouble(txtCodigoEst.getText().trim()); }
        catch (Exception ex) { error("El campo 'Código Est.' debe ser numérico."); return null; }

        try { anio = Integer.parseInt(txtAnio.getText().trim()); }
        catch (Exception ex) { error("El campo 'Año' debe ser entero."); return null; }

        try {
            String s = (txtSemestre.getText() == null) ? "" : txtSemestre.getText().trim();
            semestre = s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (Exception ex) { semestre = 0; }

        return new Row(curso, codigo, anio, semestre);
    }

    // ===== Helpers JDBC =====
    private Boolean isCursoActivo(int cursoId) {
        final String sql = "SELECT ACTIVO FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getBoolean(1); }
        } catch (SQLException ignored) {}
        return null;
    }

    private Integer getSemestreCurso(int cursoId) {
        final String sql = "SELECT SEMESTRE FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException ignored) {}
        return null;
    }

    private Integer getSemestreEst(double codigo) {
        final String sql = "SELECT SEMESTRE FROM ESTUDIANTE WHERE CODIGO=?";
        try (Connection con = H2DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, codigo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException ignored) {}
        return null;
    }

    // ===== Utilidad UI =====
    private void info(String m)  { Alert a=new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setTitle(null); a.setContentText(m); a.getDialogPane().setGraphic(null); a.showAndWait(); }
    private void error(String m) { Alert a=new Alert(Alert.AlertType.ERROR);       a.setHeaderText(null); a.setTitle(null); a.setContentText(m); a.getDialogPane().setGraphic(null); a.showAndWait(); }
    private void estado(String m){ if (lblEstado!=null) lblEstado.setText(m); }

    // ===== Row para TableView =====
    public static class Row {
        private final IntegerProperty cursoId = new SimpleIntegerProperty();
        private final DoubleProperty  codigo  = new SimpleDoubleProperty();
        private final IntegerProperty anio    = new SimpleIntegerProperty();
        private final IntegerProperty semestre= new SimpleIntegerProperty();

        public Row(int cursoId, double codigo, int anio, int semestre){
            this.cursoId.set(cursoId);
            this.codigo.set(codigo);
            this.anio.set(anio);
            this.semestre.set(semestre);
        }

        public IntegerProperty cursoIdProperty(){ return cursoId; }
        public DoubleProperty  codigoProperty(){  return codigo;  }
        public IntegerProperty anioProperty(){    return anio;    }
        public IntegerProperty semestreProperty(){return semestre;}

        public int getCursoId(){ return cursoId.get(); }
        public double getCodigo(){ return codigo.get(); }
        public int getAnio(){ return anio.get(); }
        public int getSemestre(){ return semestre.get(); }
    }
}
