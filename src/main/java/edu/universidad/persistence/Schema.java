
package edu.universidad.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea el esquema H2 en archivo. */
public class Schema {
    public static void crearTablas() { // crea tablas si no existen
        // Tabla de estudiantes con semestre
        String tEst = "CREATE TABLE IF NOT EXISTS ESTUDIANTE (" +
                "CODIGO DOUBLE PRIMARY KEY, " +
                "NOMBRES VARCHAR(120), " +
                "APELLIDOS VARCHAR(120), " +
                "EMAIL VARCHAR(180), " +
                "PROMEDIO DOUBLE, " +
                "ACTIVO BOOLEAN, " +
                "SEMESTRE INT NOT NULL" +
                ")"; // fin DDL

        // Tabla de profesor
        String tProf = "CREATE TABLE IF NOT EXISTS PROFESOR (" +
                "ID DOUBLE PRIMARY KEY, " +
                "NOMBRES VARCHAR(120), " +
                "APELLIDOS VARCHAR(120), " +
                "EMAIL VARCHAR(180), " +
                "TIPOCONTRATO VARCHAR(80)" +
                ")"; // fin DDL

        // Tabla de curso con semestre y profesor asignado
        String tCurso = "CREATE TABLE IF NOT EXISTS CURSO (" +
                "ID INT PRIMARY KEY, " +
                "NOMBRE VARCHAR(160), " +
                "ACTIVO BOOLEAN, " +
                "SEMESTRE INT NOT NULL, " +
                "PROFESOR_ID DOUBLE" +
                ")"; // fin DDL

        // Tabla de inscripcion (ya existente)
        String tIns = "CREATE TABLE IF NOT EXISTS INSCRIPCION (" +
                "ID IDENTITY PRIMARY KEY, " +
                "CURSO_ID INT NOT NULL, " +
                "ESTUDIANTE_CODIGO DOUBLE NOT NULL, " +
                "ANIO INT NOT NULL, " +
                "SEMESTRE INT NOT NULL" +
                ")"; // fin DDL

        try (Connection con = H2DB.getConnection(); // abre conexión
             Statement st = con.createStatement()) { // crea statement
            st.execute(tEst); // crea estudiantes
            st.execute(tProf); // crea profesor
            st.execute(tCurso); // crea curso
            st.execute(tIns); // crea inscripcion
        } catch (SQLException e) { // maneja error
            throw new RuntimeException("Error creando esquema H2", e); // propaga
        }
    }

    public static void limpiarTablas() { // utilidad de limpieza (opcional)
        String sql = "DELETE FROM INSCRIPCION"; // borra inscripciones
        try (Connection con = H2DB.getConnection(); // con
             Statement st = con.createStatement()) { // stmt
            st.execute(sql); // exec
        } catch (SQLException e) { // err
            throw new RuntimeException("Error limpiando tablas H2", e); // propaga
        }
    }
}
