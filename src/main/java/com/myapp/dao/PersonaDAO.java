package com.myapp.dao;

import com.myapp.db.Database;
import com.myapp.model.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public void createTableIfNotExists() {
        final String sql =
            "CREATE TABLE IF NOT EXISTS persona(" +
            " id DOUBLE PRIMARY KEY," +
            " nombres VARCHAR(100) NOT NULL," +
            " apellidos VARCHAR(100) NOT NULL," +
            " email VARCHAR(120) NOT NULL" +
            ")";
        try (Connection cn = Database.getConnection();
             Statement st = cn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando tabla persona", e);
        }
    }

    public void truncate() {
        try (Connection cn = Database.getConnection();
             Statement st = cn.createStatement()) {
            st.execute("TRUNCATE TABLE persona");
        } catch (SQLException ignored) {}
    }

    public boolean insert(Persona p) {
        final String sql = "INSERT INTO persona(id, nombres, apellidos, email) VALUES (?, ?, ?, ?)";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, p.getId());
            ps.setString(2, p.getNombres());
            ps.setString(3, p.getApellidos());
            ps.setString(4, p.getEmail());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Persona p) {
        final String sql = "UPDATE persona SET nombres=?, apellidos=?, email=? WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getEmail());
            ps.setDouble(4, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteById(double id) {
        final String sql = "DELETE FROM persona WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Persona> findAll() {
        final String sql = "SELECT id, nombres, apellidos, email FROM persona ORDER BY id";
        List<Persona> out = new ArrayList<>();
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Persona(
                        rs.getDouble("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo personas", e);
        }
        return out;
    }
}
