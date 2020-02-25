package eu.ammw.transfer.validator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountValidatorTest {

    @Test
    void shouldValidAmountPass() {
        AmountValidator.validate(new BigDecimal("12.34"));
    }

    @Test
    void shouldNegativeAmountFail() {
        assertThrows(NumberFormatException.class, () -> AmountValidator.validate(new BigDecimal("-12.34")));
    }

    @Test
    void shouldIntegralAmountPass() {
        AmountValidator.validate(new BigDecimal("12"));
    }

    @Test
    void shouldZeroPass() {
        AmountValidator.validate(BigDecimal.ZERO);
    }

    @Test
    void shouldLargeNumbersPass() {
        AmountValidator.validate(BigDecimal.valueOf(Double.MAX_VALUE));
        AmountValidator.validate(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    @Test
    void shouldCentFractionsFail() {
        assertThrows(NumberFormatException.class, () -> AmountValidator.validate(new BigDecimal("12.345")));
    }

    @Test
    void shouldScientificNotationFail() {
        assertThrows(NumberFormatException.class, () -> AmountValidator.validate(new BigDecimal("1.23e-4")));
    }
}
