package io.enfire.cipher;

/**
 * This class implements the Caesar Cipher encryption algorithm for one-key and two-key variants.
 */
public class CaesarCipher {
    // ==============================
    // Fields
    // ==============================

    /**
     * 26 lower case English alphabet.
     */
    private String alphabet = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 26 lower case English alphabet shifted by key1.
     */
    private String shiftedAlpha1;
    /**
     * 26 lower case English alphabet shifted by key2.
     */
    private String shiftedAlpha2;
    /**
     * Type of encryption (one-key or two-key).
     */
    private boolean twoKeys = false;

    // ==============================
    // Constructors
    // ==============================

    /**
     * This constructor creates one-key cipher.
     *
     * @param key The key to encrypt the message with.
     */
    public CaesarCipher(int key) {
        throwCheck(key);
        shiftedAlpha1 = shiftAlpha(key);
    }

    /**
     * This constructor creates two-key cipher.
     *
     * @param key1 The first key to encrypt the message with.
     * @param key2 The second key to encrypt the message with.
     */
    public CaesarCipher(int key1, int key2) {
        throwCheck(key1);
        throwCheck(key2);
        twoKeys = true;
        shiftedAlpha1 = shiftAlpha(key1);
        shiftedAlpha2 = shiftAlpha(key2);
    }

    // ==============================
    // Public Methods
    // ==============================

    /**
     * Encrypts the message based on one-key or two-key object instance.
     *
     * @param msg The message to encrypt.
     * @return The secret.
     */
    public String encrypt(String msg) {
        if (msg.isBlank())
            return "";

        StringBuilder encrypted = new StringBuilder(msg);
        String shiftedAlpha = shiftedAlpha1;

        for (int i = 0; i < encrypted.length(); i++) {
            char c = encrypted.charAt(i);
            boolean isUpper = Character.isUpperCase(c);
            int idx = alphabet.indexOf(Character.toLowerCase(c));

            if (idx != -1) {
                if (twoKeys) {
                    shiftedAlpha = (i % 2 == 0) ? shiftedAlpha1 : shiftedAlpha2;
                }
                c = shiftedAlpha.charAt(idx);
                if (isUpper) {
                    c = Character.toUpperCase(c);
                }
                encrypted.setCharAt(i, c);
            }
        }

        return encrypted.toString();
    }

    // ==============================
    // Private Methods
    // ==============================

    /**
     * Throws {@link KeyOutOfBoundsException} error if {@code key < 0 || key > 26}.
     *
     * @param key The key to check.
     */
    private void throwCheck(int key) {
        int min = 0;
        int max = 26;
        if (key < min || key > max) {
            String err = String.format("Encryption key must be between %d and %d (both inclusive): %d",
                    min, max, key);
            throw new KeyOutOfBoundsException(err);
        }
    }

    /**
     * Shifts the English alphabet by steps defined by the key.
     *
     * @param key The steps to shift.
     * @return The shifted alphabet.
     */
    private String shiftAlpha(int key) {
        return alphabet.substring(key) + alphabet.substring(0, key);
    }
}