
package edu.universidad.persistence; // paquete persistencia

import edu.universidad.model.Curso; // modelo curso
import java.sql.*; // JDBC
import java.util.*; // util

/** DAO de Curso con semestre y profesor asignado. */
public class CursoDAO { // clase DAO

    public void upsert(Curso c, int semestre, Double profesorId) { // inserta/actualiza
        String sql = "MERGE INTO CURSO (ID,NOMBRE,ACTIVO,SEMESTRE,PROFESOR_ID) KEY(ID) VALUES (?,?,?,?,?)"; // MERGE
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            ps.setInt(1, c.getID()); // id
            ps.setString(2, c.getNombre()); // nombre
            ps.setBoolean(3, c.isActivo()); // activo
            ps.setInt(4, semestre); // semestre
            if (profesorId == null) ps.setNull(5, Types.DOUBLE); else ps.setDouble(5, profesorId); // fk profesor
            ps.executeUpdate(); // exec
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error upsert curso", ex); // propaga
        }
    }

    public boolean eliminar(int id) { // elimina por id
        String sql = "DELETE FROM CURSO WHERE ID=?"; // delete
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            ps.setInt(1, id); // id
            return ps.executeUpdate() > 0; // filas
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error eliminando curso", ex); // propaga
        }
    }

    public List<Map<String,Object>> listarConExtras() { // lista con contador y profesor
        String sql = "SELECT c.ID,c.NOMBRE,c.ACTIVO,c.SEMESTRE,c.PROFESOR_ID," +
                     " (SELECT COUNT(*) FROM INSCRIPCION i WHERE i.CURSO_ID=c.ID) AS INSCRITOS, " +
                     " p.NOMBRES, p.APELLIDOS, p.EMAIL " +
                     " FROM CURSO c LEFT JOIN PROFESOR p ON p.ID=c.PROFESOR_ID"; // consulta
        List<Map<String,Object>> out = new ArrayList<>(); // lista
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql); // prep
             ResultSet rs = ps.executeQuery()) { // exec
            while (rs.next()) { // recorre
                Map<String,Object> m = new HashMap<>(); // fila
                m.put("ID", rs.getInt("ID")); // id
                m.put("NOMBRE", rs.getString("NOMBRE")); // nombre
                m.put("ACTIVO", rs.getBoolean("ACTIVO")); // activo
                m.put("SEMESTRE", rs.getInt("SEMESTRE")); // semestre
                m.put("PROFESOR_ID", rs.getObject("PROFESOR_ID")==null?null:rs.getDouble("PROFESOR_ID")); // profesor
                m.put("INSCRITOS", rs.getInt("INSCRITOS")); // contador
                m.put("PROF_NOMBRES", rs.getString("NOMBRES")); // nombres prof
                m.put("PROF_APELLIDOS", rs.getString("APELLIDOS")); // apellidos prof
                m.put("PROF_EMAIL", rs.getString("EMAIL")); // email prof
                out.add(m); // agrega
            }
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error listando cursos", ex); // propaga
        }
        return out; // retorna
    }

    public Integer getSemestre(int id) { // semestre del curso
        String sql = "SELECT SEMESTRE FROM CURSO WHERE ID=?"; // consulta
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error leyendo semestre de curso", ex);
        }
    }

    public void asignarProfesor(int cursoId, Double profesorId) { // asigna profesor a curso
        String sql = "UPDATE CURSO SET PROFESOR_ID=? WHERE ID=?"; // update
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            if (profesorId==null) ps.setNull(1, Types.DOUBLE); else ps.setDouble(1, profesorId); // valor
            ps.setInt(2, cursoId); // id
            ps.executeUpdate(); // exec
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error asignando profesor", ex); // propaga
        }
    }
}
