package eu.ammw.transfer.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InMemoryDatabase {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String SQL_PATH = InMemoryDatabase.class.getResource("/db_init.sql").getPath();
    private static final String DB_URL = "jdbc:h2:mem:transfer-api-db;INIT=RUNSCRIPT FROM '" + SQL_PATH + "'";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabase.class);

    public static void create() {
        try {
            Class.forName(JDBC_DRIVER);
            try (Connection conn = getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeQuery("SELECT * FROM History;");
                    LOGGER.info("Created in-memory database with structure");
                } catch (SQLException e) {
                    LOGGER.error("Could not create database structure", e);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        connection.setAutoCommit(false);
        return connection;
    }
}
