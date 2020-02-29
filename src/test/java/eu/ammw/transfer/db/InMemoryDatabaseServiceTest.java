package eu.ammw.transfer.db;

import eu.ammw.transfer.db.InMemoryDatabaseService.DatabaseServiceException;
import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryDatabaseServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @InjectMocks
    private InMemoryDatabaseService databaseService;

    @BeforeEach
    void setUp() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
    }

    @Test
    void shouldGetHistory() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery("SELECT id, account_from, account_to, amount FROM History WHERE account_from='"
                + TEST_UUID + "' OR account_to='" + TEST_UUID + "';")).thenReturn(resultSet);

        // WHEN
        databaseService.getHistory(TEST_UUID);

        // THEN
        verify(resultSet).next();
    }

    @Test
    void shouldCreateAccount() throws SQLException {
        // GIVEN
        Account account = new Account(TEST_UUID, "Test Account", BigDecimal.ZERO);

        // WHEN
        databaseService.createAccount(account);

        // THEN
        verify(statement).executeUpdate("INSERT INTO Accounts VALUES ('" + TEST_UUID + "', 'Test Account', 0);");
    }

    @Test
    void shouldUpdateAccount() throws SQLException {
        // GIVEN
        Account account = new Account(TEST_UUID, "Test Account", BigDecimal.ONE);

        // WHEN
        databaseService.updateAccount(account);

        // THEN
        verify(statement).executeUpdate("UPDATE Accounts SET name='Test Account', balance=1 WHERE id='"
                + TEST_UUID + "';");
    }

    @Test
    void shouldGetAccountById() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery("SELECT id, name, balance FROM Accounts WHERE id='"
                + TEST_UUID + "';")).thenReturn(resultSet);

        // WHEN
        Optional<Account> result = databaseService.getAccount(TEST_UUID);

        // THEN
        verify(resultSet).next();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowOnMoreThanOneAccountById() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(anyString())).thenReturn(TEST_UUID.toString());
        when(resultSet.getBigDecimal(anyString())).thenReturn(BigDecimal.ONE);
        when(statement.executeQuery("SELECT id, name, balance FROM Accounts WHERE id='"
                + TEST_UUID + "';")).thenReturn(resultSet);

        // WHEN
        DatabaseServiceException exception = assertThrows(DatabaseServiceException.class,
                () -> databaseService.getAccount(TEST_UUID));

        // THEN
        assertThat(exception).hasMessage("Multiple results for account ID " + TEST_UUID);
    }

    @Test
    void shouldGetAllAccounts() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery("SELECT id, name, balance FROM Accounts;")).thenReturn(resultSet);

        // WHEN
        databaseService.getAllAccounts();

        // THEN
        verify(resultSet).next();
    }

    @Test
    void shouldCreateTransfer() throws SQLException {
        // GIVEN
        Transfer transfer = new Transfer(TEST_UUID, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE);

        // WHEN
        databaseService.createTransfer(transfer);

        // THEN
        verify(statement).executeUpdate("INSERT INTO History VALUES ('" +
                TEST_UUID + "', '" + transfer.getFrom() + "', '" + transfer.getTo() + "', 1);");
    }
}
