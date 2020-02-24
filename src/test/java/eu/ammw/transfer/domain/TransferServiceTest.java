package eu.ammw.transfer.domain;

import eu.ammw.transfer.model.Transfer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransferServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    private TransferService transferService = new TransferService(null);

    @Test
    void shouldTransfer() {
        // GIVEN
        UUID anotherId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(123);

        // WHEN
        Transfer result = transferService.transfer(TEST_UUID, anotherId, amount);

        // THEN
        assertThat(result.getId()).isNotNull();
        assertThat(result)
                .extracting(Transfer::getFrom, Transfer::getTo, Transfer::getAmount)
                .containsExactly(TEST_UUID, anotherId, amount);
    }

    @Test
    void shouldGetHistory() {
        // WHEN
        List<Transfer> result = transferService.getHistory(TEST_UUID);

        // THEN
        assertThat(result).hasSize(2);
    }
}