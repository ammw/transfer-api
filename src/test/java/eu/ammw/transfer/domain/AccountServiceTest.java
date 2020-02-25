package eu.ammw.transfer.domain;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private DataSource dataSource;

    private AccountService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new AccountService(dataSource);
    }

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

        // WHEN
        Assertions.assertThrows(AccountNotFoundException.class, () -> service.getAccount(TEST_UUID));
    }

    @Test
    void shouldCreateNewAccount() {
        // WHEN
        Account result = service.createAccount();

        // THEN
        assertThat(result.getId()).isNotNull();
    }
}
