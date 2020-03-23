package eu.ammw.transfer.db;

import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryDatabaseService implements DataSource {
    private static final String ALL_ACCOUNTS_QUERY = "SELECT id, name, balance FROM Accounts;";
    private static final String ACCOUNT_SEARCH_QUERY = "SELECT id, name, balance FROM Accounts WHERE id=?;";
    private static final String ACCOUNT_INSERT_QUERY = "INSERT INTO Accounts VALUES (?, ?, ?);";
    private static final String ACCOUNT_UPDATE_QUERY = "UPDATE Accounts SET name=?, balance=? WHERE id=?;";
    private static final String TRANSFER_HISTORY_QUERY = "SELECT id, account_from, account_to, amount FROM History WHERE account_from=? OR account_to=?;";
    private static final String TRANSFER_INSERT_QUERY = "INSERT INTO History VALUES (?, ?, ?, ?);";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabaseService.class);

    private final Connection connection;

    private final PreparedStatement allAccountsStatement;
    private final PreparedStatement accountSearchStatement;
    private final PreparedStatement accountUpdateStatement;
    private final PreparedStatement newAccountStatement;
    private final PreparedStatement transferHistoryStatement;
    private final PreparedStatement newTransferStatement;

    public InMemoryDatabaseService(Connection connection) throws SQLException {
        this.connection = connection;
        this.allAccountsStatement = connection.prepareStatement(ALL_ACCOUNTS_QUERY);
        this.accountSearchStatement = connection.prepareStatement(ACCOUNT_SEARCH_QUERY);
        this.accountUpdateStatement = connection.prepareStatement(ACCOUNT_UPDATE_QUERY);
        this.newAccountStatement = connection.prepareStatement(ACCOUNT_INSERT_QUERY);
        this.transferHistoryStatement = connection.prepareStatement(TRANSFER_HISTORY_QUERY);
        this.newTransferStatement = connection.prepareStatement(TRANSFER_INSERT_QUERY);
    }

    @Override
    public void createAccount(Account account) {
        try {
            newAccountStatement.clearParameters();
            newAccountStatement.setString(1, account.getId().toString());
            newAccountStatement.setString(2, account.getName());
            newAccountStatement.setBigDecimal(3, account.getBalance());
            newAccountStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not create account", e);
        }
    }

    @Override
    public void updateAccount(Account account) {
        try {
            accountUpdateStatement.clearParameters();
            accountUpdateStatement.setString(1, account.getName());
            accountUpdateStatement.setBigDecimal(2, account.getBalance());
            accountUpdateStatement.setString(3, account.getId().toString());
            accountUpdateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not update account", e);
        }
    }

    @Override
    public Optional<Account> getAccount(UUID id) {
        try {
            accountSearchStatement.clearParameters();
            accountSearchStatement.setString(1, id.toString());
            ResultSet resultSet = accountSearchStatement.executeQuery();
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
            ResultSet resultSet = allAccountsStatement.executeQuery();
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
            transferHistoryStatement.clearParameters();
            transferHistoryStatement.setString(1, accountId.toString());
            transferHistoryStatement.setString(2, accountId.toString());
            ResultSet resultSet = transferHistoryStatement.executeQuery();

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
        try {
            newTransferStatement.clearParameters();
            newTransferStatement.setString(1, transfer.getId().toString());
            newTransferStatement.setString(2, transfer.getFrom().toString());
            newTransferStatement.setString(3, transfer.getTo().toString());
            newTransferStatement.setBigDecimal(4, transfer.getAmount());
            newTransferStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseServiceException("Could not create transfer", e);
        }
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
