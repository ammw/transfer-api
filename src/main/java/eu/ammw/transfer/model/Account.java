package eu.ammw.transfer.model;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public class Account {
    private final UUID id;
    private String name;
    private BigDecimal balance;

    public Account(UUID id) {
        this.id = ofNullable(id).orElse(UUID.randomUUID());
    }

    public Account(UUID id, String name, BigDecimal balance) {
        this(id);
        this.name = name;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
