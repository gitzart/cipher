package io.enfire.cipher;

/**
 * This exception is thrown when an encryption key is out of bounds.
 */
public class KeyOutOfBoundsException extends RuntimeException {
    public KeyOutOfBoundsException(String errorMessage) {
        super(errorMessage);
    }
}

