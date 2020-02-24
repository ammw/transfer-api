package eu.ammw.transfer.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InMemoryDatabase {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:transfer-api-db";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabase.class);

    public static void create() {
        try {
            Class.forName(JDBC_DRIVER);
            try (Connection conn = getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    createStructure(statement);
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

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static void createStructure(Statement statement) throws SQLException {
        String sql = "CREATE TABLE Accounts " +
                "(id UUID not NULL, " +
                " name VARCHAR not NULL," +
                " balance DECIMAL not NULL default 0," +
                " PRIMARY KEY ( id ));" +
                "CREATE TABLE History " +
                "(id UUID not NULL, " +
                " account_from UUID not NULL, " +
                " account_to UUID not NULL, " +
                " amount DECIMAL not NULL," +
                " PRIMARY KEY ( id ));" +
                "ALTER TABLE History\n" +
                " ADD FOREIGN KEY (account_from) \n" +
                " REFERENCES Accounts(id);" +
                "ALTER TABLE History\n" +
                " ADD FOREIGN KEY (account_to) \n" +
                " REFERENCES Accounts(id);";
        statement.executeUpdate(sql);
        statement.getConnection().commit();
    }
}
