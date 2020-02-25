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

    public Account getAccount(UUID id) throws AccountNotFoundException {
        return dataSource.getAccount(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account createAccount() {
        Account account = new Account(null, "Test" + System.currentTimeMillis(), BigDecimal.ZERO);
        dataSource.createAccount(account);
        dataSource.commit();
        LOGGER.info("Created account {}", account.getId());
        return account;
    }

    public boolean accountExists(UUID id) {
        return dataSource.getAccount(id).isPresent();
    }
}
