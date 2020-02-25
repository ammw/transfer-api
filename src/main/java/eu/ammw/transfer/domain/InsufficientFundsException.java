package eu.ammw.transfer.domain;

import eu.ammw.transfer.model.Account;

import java.math.BigDecimal;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(Account account, BigDecimal amount) {
        super(String.format("Account '%s' (%s) has insufficient funds to transfer out %s", account.getName(), account.getId(), amount));
    }
}
