package eu.ammw.transfer.db;

import eu.ammw.transfer.db.InMemoryDatabaseService.DatabaseServiceException;
import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private PreparedStatement statement;

    private InMemoryDatabaseService databaseService;

    @BeforeEach
    void setUp() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        databaseService = new InMemoryDatabaseService(connection);
    }

    @Test
    void shouldGetHistory() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        // WHEN
        databaseService.getHistory(TEST_UUID);

        // THEN
        verify(statement).clearParameters();
        verify(statement).setString(1, TEST_UUID.toString());
        verify(statement).setString(2, TEST_UUID.toString());
        verifyNoMoreInteractions(statement);
        verify(resultSet).next();
    }

    @Test
    void shouldCreateAccount() throws SQLException {
        // GIVEN
        Account account = new Account(TEST_UUID, "Test Account", BigDecimal.ZERO);

        // WHEN
        databaseService.createAccount(account);

        // THEN
        verify(statement).clearParameters();
        verify(statement).setString(1, TEST_UUID.toString());
        verify(statement).setString(2, "Test Account");
        verify(statement).setBigDecimal(3, BigDecimal.ZERO);
        verify(statement).executeUpdate();
        verifyNoMoreInteractions(statement);
    }

    @Test
    void shouldUpdateAccount() throws SQLException {
        // GIVEN
        Account account = new Account(TEST_UUID, "Test Account", BigDecimal.ONE);

        // WHEN
        databaseService.updateAccount(account);

        // THEN
        verify(statement).clearParameters();
        verify(statement).setString(1, "Test Account");
        verify(statement).setBigDecimal(2, BigDecimal.ONE);
        verify(statement).setString(3, TEST_UUID.toString());
        verify(statement).executeUpdate();
        verifyNoMoreInteractions(statement);
    }

    @Test
    void shouldGetAccountById() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        // WHEN
        Optional<Account> result = databaseService.getAccount(TEST_UUID);

        // THEN
        verify(resultSet).next();
        verify(statement).clearParameters();
        verify(statement).setString(1, TEST_UUID.toString());
        verifyNoMoreInteractions(statement);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowOnMoreThanOneAccountById() throws SQLException {
        // GIVEN
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(anyString())).thenReturn(TEST_UUID.toString());
        when(resultSet.getBigDecimal(anyString())).thenReturn(BigDecimal.ONE);
        when(statement.executeQuery()).thenReturn(resultSet);

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
        when(statement.executeQuery()).thenReturn(resultSet);

        // WHEN
        databaseService.getAllAccounts();

        // THEN
        verifyNoMoreInteractions(statement);
        verify(resultSet).next();
    }

    @Test
    void shouldCreateTransfer() throws SQLException {
        // GIVEN
        Transfer transfer = new Transfer(TEST_UUID, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE);

        // WHEN
        databaseService.createTransfer(transfer);

        // THEN
        verify(statement).clearParameters();
        verify(statement).setString(1, TEST_UUID.toString());
        verify(statement).setString(2, transfer.getFrom().toString());
        verify(statement).setString(3, transfer.getTo().toString());
        verify(statement).setBigDecimal(4, BigDecimal.ONE);
        verify(statement).executeUpdate();
        verifyNoMoreInteractions(statement);
    }
}
