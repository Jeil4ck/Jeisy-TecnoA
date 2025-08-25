package com.myapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    // En memoria: se borra todo al terminar la app (evita duplicados entre ejecuciones)
    private static final String URL = "jdbc:h2:mem:universidad;DB_CLOSE_DELAY=-1";
    // Si quieres archivo en disco, comenta la de arriba y usa:
    // private static final String URL = "jdbc:h2:./data/universidad";

    private static final String USER = "sa";
    private static final String PASS = "";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
