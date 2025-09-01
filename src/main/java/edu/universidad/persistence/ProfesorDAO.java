
package edu.universidad.persistence; // persistencia

import edu.universidad.model.Profesor; // modelo
import java.sql.*; // JDBC
import java.util.*; // util

/** DAO de Profesor con persistencia en H2. */
public class ProfesorDAO { // clase

    public void upsert(Profesor p) { // inserta/actualiza por ID
        String sql = "MERGE INTO PROFESOR (ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO) KEY(ID) VALUES (?,?,?,?,?)"; // MERGE
        try (Connection con = H2DB.getConnection(); // con
             PreparedStatement ps = con.prepareStatement(sql)) { // prep
            ps.setDouble(1, p.getID()); // id
            ps.setString(2, p.getNombres()); // nombres
            ps.setString(3, p.getApellidos()); // apellidos
            ps.setString(4, p.getEmail()); // email
            ps.setString(5, p.getTipoContrato()); // contrato
            ps.executeUpdate(); // exec
        } catch (SQLException ex) { // err
            throw new RuntimeException("Error upsert profesor", ex); // propaga
        }
    }

    public boolean eliminar(double id) { // elimina
        String sql = "DELETE FROM PROFESOR WHERE ID=?"; // delete
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, id); // id
            return ps.executeUpdate() > 0; // filas
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando profesor", ex);
        }
    }

    public List<Profesor> listar() { // lista
        String sql = "SELECT ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO FROM PROFESOR"; // consulta
        List<Profesor> out = new ArrayList<>(); // lista
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double id = rs.getDouble(1); // id
                String nom = rs.getString(2); // nombres
                String ape = rs.getString(3); // apellidos
                String email = rs.getString(4); // email
                String tipo = rs.getString(5); // contrato
                out.add(new Profesor(id, nom, ape, email, tipo)); // crea entidad
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando profesores", ex);
        }
        return out; // retorno
    }
}
