package eu.ammw.transfer.rest;

import eu.ammw.transfer.domain.TransferService;
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
    void shouldDoTransfer() {
        // GIVEN
        Transfer expected = mock(Transfer.class);
        when(transferService.transfer(any(UUID.class), any(UUID.class), any(BigDecimal.class))).thenReturn(expected);

        // WHEN
        Object result = transferController.transfer(request, response);

        // THEN
        verify(response).type("application/json");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldGetAccountFromService() {
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
}