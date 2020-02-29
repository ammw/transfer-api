package eu.ammw.transfer.domain;

import eu.ammw.transfer.db.DataSource;
import eu.ammw.transfer.model.Account;
import eu.ammw.transfer.model.Transfer;
import eu.ammw.transfer.validator.AmountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private final DataSource dataSource;
    private final AccountService accountService;

    public TransferService(DataSource dataSource, AccountService accountService) {
        this.dataSource = dataSource;
        this.accountService = accountService;
    }

    public synchronized Transfer transfer(UUID from, UUID to, BigDecimal amount)
            throws InsufficientFundsException, NegativeTransferException, AccountNotFoundException, TransferException {
        if (amount.signum() <= 0) {
            throw new NegativeTransferException(amount);
        }
        AmountValidator.validate(amount);
        if (from.equals(to)) {
            throw new TransferException("Cannot transfer money to yourself!");
        }

        Account accountTo = accountService.getAccount(to);
        Account accountFrom = accountService.getAccount(from);
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountFrom, amount);
        }

        Transfer transfer = new Transfer(from, to, amount);
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));

        try {
            dataSource.createTransfer(transfer);
            dataSource.updateAccount(accountFrom);
            dataSource.updateAccount(accountTo);
            dataSource.commit();
            LOGGER.info("Transferred {} from {} to {}", amount, from, to);
            return transfer;
        } catch (Exception e) {
            dataSource.rollback();
            throw new TransferException("Exception while transferring", e);
        }
    }

    public List<Transfer> getHistory(UUID id) throws AccountNotFoundException {
        if (accountService.accountExists(id)) {
            return dataSource.getHistory(id);
        }
        throw new AccountNotFoundException(id);
    }

    public synchronized void deposit(UUID accountId, BigDecimal amount)
            throws AccountNotFoundException, TransferException {
        AmountValidator.validate(amount);
        Account account = accountService.getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        try {
            dataSource.updateAccount(account);
            dataSource.createTransfer(new Transfer(accountId, accountId, amount));
            dataSource.commit();
            LOGGER.info("Deposited {} on '{}' account ({})", amount, account.getName(), account.getId());
        } catch (Exception e) {
            dataSource.rollback();
            throw new TransferException("Exception during deposit", e);
        }
    }

    public synchronized void withdraw(UUID accountId, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException, TransferException {
        AmountValidator.validate(amount);
        Account account = accountService.getAccount(accountId);
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientFundsException(account, amount);
        }
        account.setBalance(account.getBalance().subtract(amount));
        try {
            dataSource.updateAccount(account);
            dataSource.createTransfer(new Transfer(accountId, accountId, amount.negate()));
            dataSource.commit();
            LOGGER.info("Withdrawal of {} from '{}' account ({})", amount, account.getName(), account.getId());
        } catch (Exception e) {
            dataSource.rollback();
            throw new TransferException("Exception during withdrawal", e);
        }
    }
}
