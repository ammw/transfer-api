package eu.ammw.transfer.db;

import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataSource {
    void commit();

    void rollback();

    void createAccount(Account account);

    void updateAccount(Account account);

    Optional<Account> getAccount(UUID id);

    List<Account> getAllAccounts();

    List<Transfer> getHistory(UUID accountId);

    void createTransfer(Transfer transfer);
}
