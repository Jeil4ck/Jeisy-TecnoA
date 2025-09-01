
package edu.universidad.persistence; // paquete de persistencia

import edu.universidad.model.Estudiante; // modelo Estudiante
import java.sql.*; // JDBC
import java.util.*; // utilidades

/** DAO de Estudiante con persistencia en H2 (archivo). */
public class EstudianteDAO { // define clase DAO

    public void upsert(Estudiante e, int semestre) { // inserta/actualiza por codigo
        String sql = "MERGE INTO ESTUDIANTE (CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,ACTIVO,SEMESTRE) KEY(CODIGO) VALUES (?,?,?,?,?,?,?)"; // sentencia MERGE
        try (Connection con = H2DB.getConnection(); // abre conexión
             PreparedStatement ps = con.prepareStatement(sql)) { // prepara sentencia
            ps.setDouble(1, e.getCodigo()); // valor de clave
            ps.setString(2, e.getNombres()); // nombres
            ps.setString(3, e.getApellidos()); // apellidos
            ps.setString(4, e.getEmail()); // email
            ps.setDouble(5, e.getPromedio()); // promedio
            ps.setBoolean(6, e.isActivo()); // activo
            ps.setInt(7, semestre); // semestre
            ps.executeUpdate(); // ejecuta
        } catch (SQLException ex) { // captura errores
            throw new RuntimeException("Error upsert estudiante", ex); // propaga
        }
    }

    public boolean eliminar(double codigo) { // elimina por codigo
        String sql = "DELETE FROM ESTUDIANTE WHERE CODIGO=?"; // sentencia delete
        try (Connection con = H2DB.getConnection(); // conexión
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            ps.setDouble(1, codigo); // set clave
            return ps.executeUpdate() > 0; // retorna si borró
        } catch (SQLException ex) { // error
            throw new RuntimeException("Error eliminando estudiante", ex); // propaga
        }
    }

    public List<Estudiante> listar() { // lista todos
        String sql = "SELECT CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,ACTIVO,SEMESTRE FROM ESTUDIANTE"; // consulta
        List<Estudiante> out = new ArrayList<>(); // lista
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql); // prep
             ResultSet rs = ps.executeQuery()) { // ejecuta
            while (rs.next()) { // recorre
                double codigo = rs.getDouble(1); // codigo
                String nombres = rs.getString(2); // nombres
                String apellidos = rs.getString(3); // apellidos
                String email = rs.getString(4); // email
                double promedio = rs.getDouble(5); // promedio
                boolean activo = rs.getBoolean(6); // activo
                int semestre = rs.getInt(7); // semestre
                out.add(new Estudiante(codigo, nombres, apellidos, email, codigo, null, activo, promedio)); // crea entidad
            }
        } catch (SQLException ex) { // error
            throw new RuntimeException("Error listando estudiantes", ex); // propaga
        }
        return out; // retorna
    }

    public Integer getSemestre(double codigo) { // obtiene semestre por codigo
        String sql = "SELECT SEMESTRE FROM ESTUDIANTE WHERE CODIGO=?"; // consulta
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            ps.setDouble(1, codigo); // set
            try (ResultSet rs = ps.executeQuery()) { // exec
                if (rs.next()) return rs.getInt(1); // semestre
                return null; // no encontrado
            }
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error leyendo semestre de estudiante", ex); // propaga
        }
    }
}
