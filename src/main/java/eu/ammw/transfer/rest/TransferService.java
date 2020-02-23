package eu.ammw.transfer.rest;

import eu.ammw.transfer.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private final AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    Transfer transfer(UUID from, UUID to, long amount) {
        LOGGER.info("Transferred {} from {} to {}", amount, from, to);
        return new Transfer(from, to, amount);
    }

    List<Transfer> getHistory(UUID id) {
        LOGGER.info("Found {} transfers for {}", 2, id);
        return Arrays.asList(new Transfer(id, UUID.randomUUID(), 5000L),
                new Transfer(UUID.randomUUID(), id, 3000L));
    }
}
