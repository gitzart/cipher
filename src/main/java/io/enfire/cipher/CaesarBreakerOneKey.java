package io.enfire.cipher;

import java.io.IOException;

/**
 * This class implements one-key Caesar Cipher decryption algorithm.
 */
public class CaesarBreakerOneKey extends Breaker {
    // ==============================
    // Fields
    // ==============================

    /**
     * This field will contain the key used in encryption when the secret
     * is successfully decrypted. -1 means the decryption failed or hasn't started yet.
     */
    public int key = -1;

    // ==============================
    // Constructors
    // ==============================

    /**
     * Creates a new {@code CaesarHacker} instance.
     *
     * @param file Dictionary file path.
     * @throws IOException if the file path does not exist.
     */
    public CaesarBreakerOneKey(String file) throws IOException {
        super(file);
    }

    // ==============================
    // Public Methods
    // ==============================

    /**
     * Checks if the encrypted message can be decrypted.
     *
     * @return {@code boolean} decryption result.
     */
    @Override
    public boolean canDecrypt() {
        return key > -1 && !decrypted.isBlank();
    }

    /**
     * Decrypts the secret with appropriate algorithms: Frequency Analysis or Brute Force.
     *
     * @param secret The secret to decrypt.
     */
    @Override
    public void decrypt(String secret) {
        if (secret.isBlank())
            return;

        // Reset the instance. Make it ready for another round.
        key = -1;
        decrypted = "";

        secret = read(secret);
        int threshold = calcThreshold(secret);
        int wordLimit = 50;

        String[] r;
        if (secret.split("\\s+").length < wordLimit) {
            r = bruteForce(secret, threshold);
        } else {
            r = frequencyAnalysis(secret, threshold);
        }

        key = getKey(Integer.parseInt(r[0]));
        decrypted = r[1];
    }

    // ==============================
    // Private Methods
    // ==============================

    /**
     * Decrypts the secret by learning the letters' frequencies.
     *
     * <p>Algorithm: Given the secret, count each letter frequency and get the most
     * frequent letter (call X). Then, assume that X can be each one of {@link Breaker#freqLetters}.
     * Find the possible key from that assumption and decrypt the secret until it makes or breaks.</p>
     *
     * @param secret    The secret to decrypt.
     * @param threshold Acceptance level to decide whether the decrypted text is English.
     * @return An array containing the decryption key and the decrypted message.
     */
    private String[] frequencyAnalysis(String secret, int threshold) {
        int[] counter = count(secret);
        int target = getMaxCount(counter);

        for (int i = 0; i < freqLetters.length(); i++) {
            int freqLetterIndex = alphabet.indexOf(freqLetters.charAt(i));
            int key = calcKey(target, freqLetterIndex);
            String decrypted = new CaesarCipher(key).encrypt(secret);
            if (isEnglish(decrypted, threshold)) {
                return new String[]{Integer.toString(key), decrypted};
            }
        }
        return new String[]{"-1", ""};
    }

    /**
     * Decrypts the secret by trying with all possible keys.
     *
     * @param secret    The secret to decrypt.
     * @param threshold Acceptance level to decide whether the decrypted text is English.
     * @return An array containing the decryption key and the decrypted message.
     */
    private String[] bruteForce(String secret, int threshold) {
        for (int i = 0; i < alphabet.length(); i++) {
            String decrypted = new CaesarCipher(i).encrypt(secret);
            if (isEnglish(decrypted, threshold)) {
                return new String[]{Integer.toString(i), decrypted};
            }
        }
        return new String[]{"-1", ""};
    }

    /**
     * Gets the key used in encryption.
     *
     * <p>Decryption key comes from decrypted text which is shifted by encryption key.
     * Hence, by nature, decryption keys differ from encryption keys by, at most, 26 letters.
     * Shifting another 26 letters can get the encryption key.</p>
     *
     * <pre>{@code
     * message: hey | key: 09 => qnh
     * secret:  qnh | key: 17 => hey
     * 26 - 17 = 9
     * }</pre>
     *
     * @param key The key used in decryption.
     * @return The encryption key.
     */
    private int getKey(int key) {
        return (key > 0) ? alphabet.length() - key : key;
    }
}