package io.enfire.cipher;

import java.io.IOException;

/**
 * This class implements two-key Caesar Cipher decryption algorithm.
 */
public class CaesarBreakerTwoKey extends Breaker {
    // ==============================
    // Fields
    // ==============================

    /**
     * This field will contain the two keys used in encryption when the secret
     * is successfully decrypted. -1 means the decryption failed or hasn't started yet.
     */
    public int[] key = new int[]{-1, -1};

    // ==============================
    // Constructors
    // ==============================

    /**
     * Creates a new {@code CaesarHacker} instance.
     *
     * @param file Dictionary file path.
     * @throws IOException if the file path does not exist.
     */
    public CaesarBreakerTwoKey(String file) throws IOException {
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
        return key[0] > -1 && key[1] > -1 && !decrypted.isBlank();
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
        key[0] = -1;
        key[1] = -1;
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

        key = getKey(Integer.parseInt(r[0]), Integer.parseInt(r[1]));
        decrypted = r[2];
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
     * @return An array containing the two decryption keys and the decrypted message.
     */
    private String[] frequencyAnalysis(String secret, int threshold) {
        String[] split = split(secret);

        int[] evenCounter = count(split[0]);
        int evenTarget = getMaxCount(evenCounter);

        int[] oddCounter = count(split[1]);
        int oddTarget = getMaxCount(oddCounter);

        for (int i = 0; i < freqLetters.length(); i++) {
            int freqLetterIndex = alphabet.indexOf(freqLetters.charAt(i));

            int key1 = calcKey(evenTarget, freqLetterIndex);
            String evenDecrypted = new CaesarCipher(key1).encrypt(split[0]);

            int key2 = calcKey(oddTarget, freqLetterIndex);
            String oddDecrypted = new CaesarCipher(key2).encrypt(split[1]);

            String decrypted = join(evenDecrypted, oddDecrypted);
            if (isEnglish(decrypted, threshold)) {
                return new String[]{Integer.toString(key1), Integer.toString(key2), decrypted};
            }
        }
        return new String[]{"-1", "-1", ""};
    }

    /**
     * Decrypts the secret by trying with all possible keys.
     *
     * @param secret    The secret to decrypt.
     * @param threshold Acceptance level to decide whether the decrypted text is English.
     * @return An array containing the two decryption keys and the decrypted message.
     */
    private String[] bruteForce(String secret, int threshold) {
        String[] split = split(secret);
        String[] evenDecrypted = new String[26];
        String[] oddDecrypted = new String[26];

        // First, encrypt each half with 26 keys (0-25).
        for (int i = 0; i < alphabet.length(); i++) {
            CaesarCipher caesar = new CaesarCipher(i);
            evenDecrypted[i] = caesar.encrypt(split[0]);
            oddDecrypted[i] = caesar.encrypt(split[1]);
        }

        // Then, join each of evenDecrypted with all of oddDecrypted.
        // In total, that'll be 26*26 = 676 iterations.
        for (int i = 0; i < evenDecrypted.length; i++) {
            for (int j = 0; j < oddDecrypted.length; j++) {
                String decrypted = join(evenDecrypted[i], oddDecrypted[j]);
                if (isEnglish(decrypted, threshold)) {
                    return new String[]{Integer.toString(i), Integer.toString(j), decrypted};
                }
            }
        }
        return new String[]{"-1", "-1", ""};
    }

    /**
     * Splits the text at even and odd positions.
     *
     * @param text The text to split.
     * @return The split array.
     */
    private String[] split(String text) {
        String[] arr = new String[]{"", ""};
        for (int i = 0; i < text.length(); i++) {
            int cursor = i % 2;
            arr[cursor] += text.charAt(i);
        }
        return arr;
    }

    /**
     * Joins two strings at even and odd positions.
     *
     * @param first  The first string to join.
     * @param second The second string to join.
     * @return The joined string.
     */
    private String join(String first, String second) {
        StringBuilder joined = new StringBuilder();

        // The first string is supposed to be always longer.
        for (int i = 0; i < first.length(); i++) {
            joined.append(first.charAt(i));
            if (i < second.length()) {
                joined.append(second.charAt(i));
            }
        }
        return joined.toString();
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
     * @param key1 The first key used in decryption.
     * @param key2 The second key used in decryption.
     * @return The encryption key array.
     */
    private int[] getKey(int key1, int key2) {
        key1 = (key1 > 0) ? alphabet.length() - key1 : key1;
        key2 = (key2 > 0) ? alphabet.length() - key2 : key2;
        return new int[]{key1, key2};
    }
}