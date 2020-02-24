package eu.ammw.transfer.model;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public class Transfer {
    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;

    public Transfer(UUID id, UUID from, UUID to, BigDecimal amount) {
        this.id = ofNullable(id).orElse(UUID.randomUUID());
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
