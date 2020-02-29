package eu.ammw.transfer.rest;

import eu.ammw.transfer.domain.AccountNotFoundException;
import eu.ammw.transfer.domain.AccountService;
import eu.ammw.transfer.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    private AccountController accountController;

    @Test
    void shouldGetAccountsFromService() {
        // GIVEN
        List<Account> expected = Arrays.asList(mock(Account.class), mock(Account.class));
        when(accountService.getAccounts()).thenReturn(expected);

        // WHEN
        Object result = accountController.getAccounts(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldCreateAccount() {
        // GIVEN
        Account expected = mock(Account.class);
        when(accountService.createAccount("Test")).thenReturn(expected);
        when(request.body()).thenReturn("{\"name\":\"Test\"}");

        // WHEN
        Object result = accountController.createAccount(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldReturnBadRequestWhenCreateAccountIncorrect() {
        // WHEN
        Object result = accountController.createAccount(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldReturnBadRequestWhenAccountNameIncorrect() {
        // GIVEN
        when(accountService.createAccount("Test")).thenThrow(IllegalArgumentException.class);
        when(request.body()).thenReturn("{\"name\":\"Test\"}");

        // WHEN
        Object result = accountController.createAccount(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldGetAccountFromService() throws Exception {
        // GIVEN
        Account expected = mock(Account.class);
        when(accountService.getAccount(TEST_UUID)).thenReturn(expected);
        when(request.params("id")).thenReturn(TEST_UUID.toString());

        // WHEN
        Object result = accountController.getAccount(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldReturnNotFoundWhenNoAccount() throws Exception {
        // GIVEN
        when(accountService.getAccount(TEST_UUID)).thenThrow(AccountNotFoundException.class);
        when(request.params("id")).thenReturn(TEST_UUID.toString());

        // WHEN
        Object result = accountController.getAccount(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(404);
        assertThat(result).isEqualTo("Account not found!");
    }

    @Test
    void shouldReturnBadRequestOnInvalidId() {
        // GIVEN
        when(request.params("id")).thenReturn("nonsense");

        // WHEN
        Object result = accountController.getAccount(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }
}
