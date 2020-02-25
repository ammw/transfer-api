package eu.ammw.transfer.validator;

import java.math.BigDecimal;

public class AmountValidator {

    private AmountValidator() {}

    public static void validate(BigDecimal amount) {
        if (amount.scale() > 2) {
            throw new NumberFormatException("Invalid amount of money: " + amount);
        }
    }
}
