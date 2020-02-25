package eu.ammw.transfer.domain;

import java.util.UUID;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(UUID id) {
        super("No account with ID " + id);
    }
}
