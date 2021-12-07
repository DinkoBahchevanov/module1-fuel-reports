package module1.fuelreports.business.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseConnectionService {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerService.class.getSimpleName());

    private Connection conn;
    private Statement stmt;

    private static final String DB_MAIN_URL = "jdbc:mysql://localhost/fuel_reports";
    private static final String DB_START_URL = "jdbc:mysql://localhost/";
    private static final String USER = "root";
    private static final String PASS = "12345";

    public void connectStartingUrl() {
        try {
            conn = DriverManager.getConnection(DB_START_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void connectMainUrl() {
        try {
            conn = DriverManager.getConnection(DB_MAIN_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (SQLException e) {
           LOGGER.severe(e.getMessage());
        }
    }

    public Statement getStmt() {
        return stmt;
    }

    public Connection getConnection() {
        return this.conn;
    }
}
