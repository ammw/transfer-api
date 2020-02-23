package eu.ammw.transfer.model;

import java.util.UUID;

public class Transfer {
    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final long amount;

    public Transfer(UUID from, UUID to, long amount) {
        this.id = UUID.randomUUID();
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

    public long getAmount() {
        return amount;
    }
}
