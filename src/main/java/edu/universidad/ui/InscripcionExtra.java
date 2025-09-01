package edu.universidad.ui; // utilidades adicionales para Inscripcion sin modificar la estructura base

import edu.universidad.model.Curso; // modelo Curso
import edu.universidad.model.Estudiante; // modelo Estudiante
import edu.universidad.model.Inscripcion; // modelo Inscripcion
import edu.universidad.persistence.InscripcionDAO; // DAO existente
import edu.universidad.persistence.H2DB; // acceso a conexión H2

import java.sql.*; // JDBC para operaciones directas
import java.util.ArrayList; // lista dinámica
import java.util.List; // interfaz de lista

/**
 * Clase de apoyo para extender CRUD sin romper nombres/estructura original.
 * Aquí agregamos métodos "sin catálogos" y operaciones update/delete por coincidencia exacta.
 */
public class InscripcionExtra { // clase helper estática

    public static List<Inscripcion> listarSinCatalogos(InscripcionDAO dao) { // lista creando objetos mínimos
        List<Inscripcion> out = new ArrayList<>(); // lista a retornar
        String sql = "SELECT CURSO_ID, ESTUDIANTE_CODIGO, ANIO, SEMESTRE FROM INSCRIPCION"; // consulta base
        try (Connection con = H2DB.getConnection(); // obtiene conexión H2
             PreparedStatement ps = con.prepareStatement(sql)) { // prepara la consulta
            try (ResultSet rs = ps.executeQuery()) { // ejecuta y obtiene resultados
                while (rs.next()) { // recorre filas
                    int cursoId = rs.getInt("CURSO_ID"); // lee id de curso
                    double codEst = rs.getDouble("ESTUDIANTE_CODIGO"); // lee código de estudiante
                    int anio = rs.getInt("ANIO"); // lee año
                    int semestre = rs.getInt("SEMESTRE"); // lee semestre
                    Curso c = new Curso(cursoId, null, null, true); // construye curso mínimo
                    Estudiante e = new Estudiante(codEst, null, null, null, codEst, null, true, 0); // construye estudiante mínimo
                    out.add(new Inscripcion(c, anio, semestre, e)); // añade a la lista
                }
            }
        } catch (SQLException ex) { // captura errores SQL
            throw new RuntimeException("Error listando inscripciones (sin catálogos)", ex); // re-lanza como runtime
        }
        return out; // retorna resultados
    }

    public static boolean eliminar(InscripcionDAO dao, Inscripcion i) { // elimina por coincidencia exacta de campos
        String sql = "DELETE FROM INSCRIPCION WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=? AND ANIO=? AND SEMESTRE=?"; // sentencia de borrado
        try (Connection con = H2DB.getConnection(); // conexión H2
             PreparedStatement ps = con.prepareStatement(sql)) { // prepara sentencia
            ps.setInt(1, i.getCurso().getID()); // setea id curso
            ps.setDouble(2, i.getEstudiante().getCodigo()); // setea código estudiante
            ps.setInt(3, i.getAnio()); // setea año
            ps.setInt(4, i.getSemestre()); // setea semestre
            return ps.executeUpdate() > 0; // ejecuta y retorna si borró filas
        } catch (SQLException ex) { // errores SQL
            throw new RuntimeException("Error eliminando inscripción", ex); // re-lanza como runtime
        }
    }

    public static int actualizar(InscripcionDAO dao, Inscripcion before, Inscripcion after) { // actualiza un registro
        String sql = "UPDATE INSCRIPCION SET CURSO_ID=?, ESTUDIANTE_CODIGO=?, ANIO=?, SEMESTRE=? " + // sentencia de actualización
                "WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=? AND ANIO=? AND SEMESTRE=?"; // filtro por valores anteriores
        try (Connection con = H2DB.getConnection(); // conexión H2
             PreparedStatement ps = con.prepareStatement(sql)) { // prepara sentencia
            ps.setInt(1, after.getCurso().getID()); // nuevo curso
            ps.setDouble(2, after.getEstudiante().getCodigo()); // nuevo código
            ps.setInt(3, after.getAnio()); // nuevo año
            ps.setInt(4, after.getSemestre()); // nuevo semestre
            ps.setInt(5, before.getCurso().getID()); // curso previo
            ps.setDouble(6, before.getEstudiante().getCodigo()); // código previo
            ps.setInt(7, before.getAnio()); // año previo
            ps.setInt(8, before.getSemestre()); // semestre previo
            return ps.executeUpdate(); // ejecuta y retorna número de filas afectadas
        } catch (SQLException ex) { // errores SQL
            throw new RuntimeException("Error actualizando inscripción", ex); // re-lanza
        }
    }
}
