package eu.ammw.transfer.domain;

import eu.ammw.transfer.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    public List<Account> getAccounts() {
        Account account1 = new Account(UUID.fromString("00000000-1111-2222-3333-444444444444"));
        Account account2 = new Account(UUID.fromString("99999999-1111-2222-3333-444444444444"));
        LOGGER.info("Found 2 accounts");
        return Arrays.asList(account1, account2);
    }

    public Account getAccount(UUID id) {
        LOGGER.info("Found account {}", id);
        return new Account(id);
    }

    public Account createAccount() {
        Account account = new Account(null);
        LOGGER.info("Created account {}", account.getId());
        return account;
    }
}
