package eu.ammw.transfer.domain;

import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private final AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Transfer transfer(UUID from, UUID to, BigDecimal amount) {
        LOGGER.info("Transferred {} from {} to {}", amount, from, to);
        return new Transfer(null, from, to, amount);
    }

    public List<Transfer> getHistory(UUID id) {
        LOGGER.info("Found {} transfers for {}", 2, id);
        return Arrays.asList(new Transfer(null, id, UUID.randomUUID(), BigDecimal.valueOf(500)),
                new Transfer(null, UUID.randomUUID(), id, BigDecimal.valueOf(300)));
    }
}
