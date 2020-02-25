package eu.ammw.transfer.validator;

/**
 * Validate name so that it doesn't contain illegal characters
 */
public class NameValidator {

    private NameValidator() {}

    public static void validate(String name) {
        if (name == null || !name.matches("[\\w\\p{L} -]+")) {
            throw new IllegalArgumentException("Illegal name: " + name);
        }
    }
}
