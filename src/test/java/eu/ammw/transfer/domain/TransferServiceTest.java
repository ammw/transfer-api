package eu.ammw.transfer.domain;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.db.InMemoryDatabaseService;
import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private DataSource dataSource;

    @Mock
    private AccountService accountService;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transferService = new TransferService(dataSource, accountService);
    }

    @Test
    void shouldTransfer() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ONE;
        when(accountService.getAccount(TEST_UUID)).thenReturn(new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN));
        when(accountService.getAccount(anotherId)).thenReturn(new Account(anotherId, "John Doe", BigDecimal.ZERO));

        // WHEN
        Transfer result = transferService.transfer(TEST_UUID, anotherId, amount);

        // THEN
        assertThat(result.getId()).isNotNull();
        assertThat(result)
                .extracting(Transfer::getFrom, Transfer::getTo, Transfer::getAmount)
                .containsExactly(TEST_UUID, anotherId, amount);
        verify(dataSource).updateAccount(new Account(TEST_UUID, "Jane Doe", BigDecimal.valueOf(9)));
        verify(dataSource).updateAccount(new Account(anotherId, "John Doe", BigDecimal.ONE));
        verify(dataSource).createTransfer(result);
        verify(dataSource).commit();
        verify(dataSource, never()).rollback();
    }

    @Test
    void shouldTransferThrowWhenAmountZero() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ZERO;
        when(accountService.getAccount(TEST_UUID)).thenReturn(new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN));
        when(accountService.getAccount(anotherId)).thenReturn(new Account(anotherId, "John Doe", BigDecimal.ZERO));

        // WHEN
        assertThrows(NegativeTransferException.class, () -> transferService.transfer(TEST_UUID, anotherId, amount));
    }

    @Test
    void shouldTransferThrowWhenAmountNegative() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ONE.negate();
        when(accountService.getAccount(TEST_UUID)).thenReturn(new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN));
        when(accountService.getAccount(anotherId)).thenReturn(new Account(anotherId, "John Doe", BigDecimal.ZERO));

        // WHEN
        assertThrows(NegativeTransferException.class, () -> transferService.transfer(TEST_UUID, anotherId, amount));
    }

    @Test
    void shouldTransferThrowWhenFundsInsufficient() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(10.01);
        when(accountService.getAccount(TEST_UUID)).thenReturn(new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN));
        when(accountService.getAccount(anotherId)).thenReturn(new Account(anotherId, "John Doe", BigDecimal.ZERO));

        // WHEN
        assertThrows(InsufficientFundsException.class, () -> transferService.transfer(TEST_UUID, anotherId, amount));
    }

    @Test
    void shouldTransferThrowWhenNoAccountFound() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ONE;
        when(accountService.getAccount(TEST_UUID)).thenThrow(AccountNotFoundException.class);

        // WHEN
        assertThrows(AccountNotFoundException.class, () -> transferService.transfer(TEST_UUID, anotherId, amount));
    }

    @Test
    void shouldTransferRollBackOnFailure() throws Exception {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ONE;
        when(accountService.getAccount(TEST_UUID)).thenReturn(new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN));
        when(accountService.getAccount(anotherId)).thenReturn(new Account(anotherId, "John Doe", BigDecimal.ZERO));
        doThrow(InMemoryDatabaseService.DatabaseServiceException.class).when(dataSource).updateAccount(any());

        // WHEN
        TransferException exception = assertThrows(TransferException.class, () -> transferService.transfer(TEST_UUID, anotherId, amount));

        // THEN
        assertThat(exception).hasCauseInstanceOf(InMemoryDatabaseService.DatabaseServiceException.class);
        verify(dataSource).rollback();
        verify(dataSource, never()).commit();
    }

    @Test
    void shouldGetHistory() throws Exception {
        // GIVEN
        List<Transfer> expected = Arrays.asList(mock(Transfer.class), mock(Transfer.class));
        when(accountService.accountExists(TEST_UUID)).thenReturn(true);
        when(dataSource.getHistory(TEST_UUID)).thenReturn(expected);

        // WHEN
        List<Transfer> result = transferService.getHistory(TEST_UUID);

        // THEN
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldGetHistoryThrowWhenAccountNotFound() {
        // GIVEN
        when(accountService.accountExists(TEST_UUID)).thenReturn(false);

        // WHEN
        assertThrows(AccountNotFoundException.class, () -> transferService.getHistory(TEST_UUID));
    }
}