package org.biblioteca.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static ConexionBD instance;
    private Connection connection;

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_digital";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789";

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    public static ConexionBD getInstance() {
        if (instance == null) {
            synchronized (ConexionBD.class) {
                if (instance == null) {
                    instance = new ConexionBD();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}