package org.example.Repository;

import org.sqlite.SQLiteConfig;

import java.sql.*;

public class DataBaseConnection {

    private static final String PROTOCOL = "jdbc:sqlite:/Library/apache-tomcat-11.0.1/webapps/currency-exchanger-API/WEB-INF/classes/DataBase/exchangeRate.db";

    public Connection getConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            return DriverManager.getConnection(PROTOCOL, config.toProperties());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
