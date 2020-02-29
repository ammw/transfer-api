package eu.ammw.transfer.db;

import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryDatabaseService implements DataSource {
    private static final String ALL_ACCOUNTS_QUERY = "SELECT id, name, balance FROM Accounts;";
    private static final String ACCOUNT_SEARCH_QUERY = "SELECT id, name, balance FROM Accounts WHERE id='%s';";
    private static final String ACCOUNT_INSERT_QUERY = "INSERT INTO Accounts VALUES ('%s', '%s', %s);";
    private static final String ACCOUNT_UPDATE_QUERY = "UPDATE Accounts SET name='%s', balance=%s WHERE id='%s';";
    private static final String TRANSFER_HISTORY_QUERY = "SELECT id, account_from, account_to, amount FROM History WHERE account_from='%s' OR account_to='%s';";
    private static final String TRANSFER_INSERT_QUERY = "INSERT INTO History VALUES ('%s', '%s', '%s', %s);";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabaseService.class);

    private Connection connection;

    public InMemoryDatabaseService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createAccount(Account account) {
        update(String.format(ACCOUNT_INSERT_QUERY, account.getId(), account.getName(), account.getBalance().toString()));
    }

    @Override
    public void updateAccount(Account account) {
        update(String.format(ACCOUNT_UPDATE_QUERY, account.getName(), account.getBalance().toString(), account.getId()));
    }

    @Override
    public Optional<Account> getAccount(UUID id) {
        try {
            ResultSet resultSet = read(String.format(ACCOUNT_SEARCH_QUERY, id));
            if (resultSet.next()) {
                Account account = getAccount(resultSet);
                if (resultSet.next()) {
                    throw new DatabaseServiceException("Multiple results for account ID " + id);
                }
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not retrieve account for ID " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Account> getAllAccounts() {
        try {
            ResultSet resultSet = read(ALL_ACCOUNTS_QUERY);
            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                accounts.add(getAccount(resultSet));
            }
            LOGGER.info("Retrieved {} accounts", accounts.size());
            return accounts;
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not retrieve accounts", e);
        }
    }

    @Override
    public List<Transfer> getHistory(UUID accountId) {
        try {
            ResultSet resultSet = read(String.format(TRANSFER_HISTORY_QUERY, accountId, accountId));
            List<Transfer> history = new ArrayList<>();
            while (resultSet.next()) {
                history.add(getTransfer(resultSet));
            }
            LOGGER.info("Retrieved {} transfers", history.size());
            return history;
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not retrieve history", e);
        }
    }

    @Override
    public void createTransfer(Transfer transfer) {
        update(String.format(TRANSFER_INSERT_QUERY, transfer.getId(), transfer.getFrom(), transfer.getTo(), transfer.getAmount().toString()));
    }

    private Account getAccount(ResultSet resultSet) throws SQLException {
        UUID uuid = UUID.fromString(resultSet.getString("id"));
        String name = resultSet.getString("name");
        BigDecimal balance = resultSet.getBigDecimal("balance");
        return new Account(uuid, name, balance);
    }

    private Transfer getTransfer(ResultSet resultSet) throws SQLException {
        UUID uuid = UUID.fromString(resultSet.getString("id"));
        UUID from = UUID.fromString(resultSet.getString("account_from"));
        UUID to = UUID.fromString(resultSet.getString("account_to"));
        BigDecimal amount = resultSet.getBigDecimal("amount");
        return new Transfer(uuid, from, to, amount);
    }

    private ResultSet read(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private void update(String query) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not update database", e);
        }
    }

    @Override
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not commit", e);
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not commit", e);
        }
    }

    public static class DatabaseServiceException extends RuntimeException {
        DatabaseServiceException(String message) {
            super(message);
        }

        DatabaseServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
