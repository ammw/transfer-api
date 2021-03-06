package eu.ammw.transfer.domain;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private AccountService service;

    @Test
    void shouldGetAccounts() {
        // GIVEN
        List<Account> expected = Arrays.asList(mock(Account.class), mock(Account.class));
        when(dataSource.getAllAccounts()).thenReturn(expected);

        // WHEN
        List<Account> result = service.getAccounts();

        // THEN
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldGetZeroAccountsWhenNotFound() {
        // GIVEN
        when(dataSource.getAllAccounts()).thenReturn(Collections.emptyList());

        // WHEN
        List<Account> result = service.getAccounts();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetAccountByUUID() throws AccountNotFoundException {
        // GIVEN
        Account expected = new Account(TEST_UUID, "Jane Doe", BigDecimal.TEN);
        when(dataSource.getAccount(TEST_UUID)).thenReturn(Optional.of(expected));

        // WHEN
        Account result = service.getAccount(TEST_UUID);

        // THEN
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenNoAccountByUUID() {
        // GIVEN
        when(dataSource.getAccount(TEST_UUID)).thenReturn(Optional.empty());

        // WHEN + THEN
        Assertions.assertThrows(AccountNotFoundException.class, () -> service.getAccount(TEST_UUID));
    }

    @Test
    void shouldCreateNewAccount() {
        // WHEN
        Account result = service.createAccount("Test");

        // THEN
        assertThat(result.getId()).isNotNull();
        assertThat(result.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getName()).isEqualTo("Test");
    }

    @Test
    void shouldNotCreateAccountWithInvalidName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.createAccount(".::#$%::."));
    }

    @Test
    void shouldReturnTrueWhenAccountExists() {
        // GIVEN
        when(dataSource.getAccount(TEST_UUID)).thenReturn(Optional.of(mock(Account.class)));

        // WHEN
        boolean result = service.accountExists(TEST_UUID);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAccountNotFound() {
        // GIVEN
        when(dataSource.getAccount(TEST_UUID)).thenReturn(Optional.empty());

        // WHEN
        boolean result = service.accountExists(TEST_UUID);

        // THEN
        assertThat(result).isFalse();
    }
}
