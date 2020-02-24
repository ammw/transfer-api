package eu.ammw.transfer.domain;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final DataSource dataSource;

    public AccountService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Account> getAccounts() {
        return dataSource.getAllAccounts();
    }

    public Account getAccount(UUID id) {
        return dataSource.getAccount(id).orElse(null);
    }

    public Account createAccount() {
        Account account = new Account(null, "Test" + System.currentTimeMillis(),
                BigDecimal.valueOf(System.currentTimeMillis() % 10000).divide(BigDecimal.valueOf(100)));
        dataSource.createAccount(account);
        LOGGER.info("Created account {}", account.getId());
        return account;
    }
}
