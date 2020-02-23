package eu.ammw.transfer.model;

import java.util.UUID;

import static java.util.Optional.ofNullable;

public class Account {
    private final UUID id;

    public Account(UUID id) {
        this.id = ofNullable(id).orElse(UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }
}
