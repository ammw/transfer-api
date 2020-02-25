package eu.ammw.transfer.domain;

import java.math.BigDecimal;

public class NegativeTransferException extends Exception {
    public NegativeTransferException(BigDecimal amount) {
        super("Attempted to transfer non-positive amount of " + amount);
    }
}
