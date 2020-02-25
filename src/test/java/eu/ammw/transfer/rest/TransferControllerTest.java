package eu.ammw.transfer.rest;

import eu.ammw.transfer.domain.*;
import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransferControllerTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    @Mock
    private TransferService transferService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    private TransferController transferController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transferController = new TransferController(transferService);
    }

    @Test
    void shouldDoTransfer() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        Transfer expected = new Transfer(from, to, BigDecimal.TEN);
        when(transferService.transfer(from, to, BigDecimal.TEN)).thenReturn(expected);
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": 10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldTransferReturnBadRequestWhenNoBody() {
        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldTransferReturnBadRequestWhenBodyInvalid() {
        // GIVEN
        when(request.body()).thenReturn("nonsense");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldTransferReturnNotFoundWhenNoAccount() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        when(transferService.transfer(from, to, BigDecimal.TEN)).thenThrow(AccountNotFoundException.class);
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": 10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(404);
        assertThat(result).isEqualTo("Account not found!");
    }

    @Test
    void shouldTransferReturnConflictWhenNoFunds() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        when(transferService.transfer(from, to, BigDecimal.TEN))
                .thenThrow(new InsufficientFundsException(new Account(from), BigDecimal.TEN));
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": 10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(409);
        assertThat(result.toString()).contains("insufficient funds");
    }

    @Test
    void shouldTransferReturnConflictWhenNegativeAmount() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        when(transferService.transfer(from, to, BigDecimal.TEN.negate()))
                .thenThrow(new NegativeTransferException(BigDecimal.TEN.negate()));
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": -10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(409);
        assertThat(result.toString()).contains("transfer non-positive amount");
    }

    @Test
    void shouldTransferReturnBadRequestWhenIncorrectAmount() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        when(transferService.transfer(from, to, BigDecimal.TEN))
                .thenThrow(NumberFormatException.class);
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": 10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldTransferReturnErrorWhenTransferFailed() throws Exception {
        // GIVEN
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        when(transferService.transfer(from, to, BigDecimal.TEN.negate()))
                .thenThrow(new TransferException("FAIL", new Exception("blablah")));
        when(request.body()).thenReturn("{\"from\": \"" + from + "\", \"to\": \"" + to + "\", \"amount\": -10}");

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(500);
        assertThat(result.toString()).isEqualTo("FAIL: blablah");
    }

    @Test
    void shouldGetAccountFromService() throws Exception {
        // GIVEN
        List<Transfer> expected = Arrays.asList(mock(Transfer.class), mock(Transfer.class));
        when(transferService.getHistory(TEST_UUID)).thenReturn(expected);
        when(request.params("id")).thenReturn(TEST_UUID.toString());

        // WHEN
        Object result = transferController.getHistory(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldReturnBadRequestOnInvalidId() {
        // GIVEN
        when(request.params("id")).thenReturn("nonsense");

        // WHEN
        Object result = transferController.getHistory(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Invalid account ID!");
    }

    @Test
    void shouldReturnNotFoundWhenServiceThrowsAccountNotFoundException() throws AccountNotFoundException {
        // GIVEN
        when(request.params("id")).thenReturn(TEST_UUID.toString());
        when(transferService.getHistory(TEST_UUID)).thenThrow(new AccountNotFoundException(TEST_UUID));

        // WHEN
        Object result = transferController.getHistory(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(404);
        assertThat(result).isEqualTo("Account not found!");
    }

    @Test
    void shouldDoDeposit() {
        // GIVEN
        UUID to = UUID.randomUUID();
        when(request.params("id")).thenReturn(to.toString());
        when(request.body()).thenReturn("{\"amount\": 10}");

        // WHEN
        Object result = transferController.deposit(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isNull();
    }

    @Test
    void shouldDepositReturnBadRequestOnInvalidAmount() throws AccountNotFoundException {
        // GIVEN
        UUID to = UUID.randomUUID();
        when(request.params("id")).thenReturn(to.toString());
        when(request.body()).thenReturn("{\"amount\": -10}");
        doThrow(NumberFormatException.class).when(transferService).deposit(to, BigDecimal.TEN.negate());

        // WHEN
        Object result = transferController.deposit(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldDepositReturnBadRequestOnInvalidBody() {
        // GIVEN
        UUID to = UUID.randomUUID();
        when(request.params("id")).thenReturn(to.toString());
        when(request.body()).thenReturn("nonsense");

        // WHEN
        Object result = transferController.deposit(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldDepositReturnBadRequestOnMissingBody() {
        // GIVEN
        UUID to = UUID.randomUUID();
        when(request.params("id")).thenReturn(to.toString());

        // WHEN
        Object result = transferController.deposit(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(400);
        assertThat(result).isEqualTo("Bad Request");
    }

    @Test
    void shouldDepositReturnNotFoundWhenNoAccount() throws AccountNotFoundException {
        // GIVEN
        UUID to = UUID.randomUUID();
        when(request.params("id")).thenReturn(to.toString());
        when(request.body()).thenReturn("{\"amount\": -10}");
        doThrow(AccountNotFoundException.class).when(transferService).deposit(to, BigDecimal.TEN.negate());

        // WHEN
        Object result = transferController.deposit(request, response);

        // THEN
        verify(response).type("text/plain");
        verify(response).status(404);
        assertThat(result).isEqualTo("Account not found!");
    }
}