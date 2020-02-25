package eu.ammw.transfer.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Transfer {
    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;

    public Transfer(UUID from, UUID to, BigDecimal amount) {
        this(UUID.randomUUID(), from, to, amount);
    }

    public Transfer(UUID id, UUID from, UUID to, BigDecimal amount) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(id, transfer.id) &&
                Objects.equals(from, transfer.from) &&
                Objects.equals(to, transfer.to) &&
                amount.compareTo(transfer.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, amount);
    }
}
