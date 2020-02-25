package eu.ammw.transfer.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {

    @Test
    void shouldValidNamePass() {
        NameValidator.validate("Aleksandra Magura-Witkowska");
    }

    @Test
    void shouldUnderscoredNamePass() {
        NameValidator.validate("secret_agent_007");
    }

    @Test
    void shouldUnicodeNamePass() {
        NameValidator.validate("Ąłękśądrą");
    }

    @Test
    void shouldInvalidNameFail() {
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validate("h4ck3r' OR 1 == 1;"));
    }
}