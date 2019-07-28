package io.enfire.cipher;

import io.enfire.util.DescStringLengthComparator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * This class includes basic decryption functionality for {@code Caesar} and {@code Vigenère} ciphers.
 */
public abstract class Breaker {
    // ==============================
    // Fields
    // ==============================

    /**
     * 26 lower case English alphabet.
     */
    protected final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 26 lower case English alphabet ordered by their descending frequencies. Cited from
     * <a href='http://pi.math.cornell.edu/~mec/2003-2004/cryptography/subs/frequencies.html'>cornell.edu</a>
     */
    protected final String freqLetters = "etaoinsrhdlucmfywgpbvkxqjz";
    /**
     * This field will contain a message when the secret is successfully decrypted.
     */
    public String decrypted = "";
    /**
     * {@code Breaker} object will consult this dictionary to decrypt the secret.
     */
    protected Dictionary dictionary;

    // ==============================
    // Constructors
    // ==============================

    /**
     * Creates a new {@code Breaker} instance.
     *
     * @param file Dictionary file path.
     * @throws IOException if the file path does not exist.
     */
    public Breaker(String file) throws IOException {
        this.dictionary = new Dictionary(file);
    }

    // ==============================
    // Public Methods
    // ==============================

    /**
     * Checks if the decryption is a success.
     *
     * @return {@code boolean} decryption result.
     */
    public abstract boolean canDecrypt();

    /**
     * Decrypts the secret.
     *
     * @param secret The secret to decrypt.
     */
    public abstract void decrypt(String secret);

    // ==============================
    // Protected Methods
    // ==============================

    /**
     * Reads the secret text from the file if the given file exists,
     * otherwise returns the text as it is.
     *
     * @param secret A secret file path or a secret text.
     * @return The secret text.
     */
    protected String read(String secret) {
        StringBuilder s = new StringBuilder();
        try {
            Reader reader = new FileReader(secret);
            int ch;
            while ((ch = reader.read()) != -1) {
                s.append((char) ch);
            }
            secret = s.toString();
            reader.close();
        } catch (IOException e) {
            // Secret is not a file.
        }
        return secret;
    }

    /**
     * Counts the frequencies of letters.
     *
     * @param text English text.
     * @return A frequency counter array.
     */
    protected int[] count(String text) {
        int[] counter = new int[26];
        for (char c : text.toCharArray()) {
            int index = alphabet.indexOf(Character.toLowerCase(c));
            if (index != -1) {
                counter[index]++;
            }
        }
        return counter;
    }

    /**
     * Gets the most frequent letter index from the counter array.
     *
     * @param counter Frequency counter array.
     * @return Most frequent letter index.
     */
    protected int getMaxCount(int[] counter) {
        int max = 0;
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] > counter[max]) {
                max = i;
            }
        }
        return max;
    }

    /**
     * Calculates the decryption key (0 - 26).
     *
     * @param freqCounterIndex Index of the most frequent letter of the frequency counter array.
     * @param freqLetterIndex  Index of one of the letters of {@link Breaker#freqLetters}.
     * @return The decryption key.
     */
    protected int calcKey(int freqCounterIndex, int freqLetterIndex) {
        int key = freqCounterIndex - freqLetterIndex;
        if (freqCounterIndex < freqLetterIndex) {
            key = alphabet.length() - (freqLetterIndex - freqCounterIndex);
        }
        return alphabet.length() - key;
    }

    /**
     * Checks if the given decrypted text is valid English.
     *
     * <p>Algorithm: Sort the words by length in descending order so that the longer words can be checked first
     * because the small ones (articles, preposition, etc.) are not distinguishable from gibberish.
     * Then, check word by word until it reaches a certain point where we can accept the validity of the text.</p>
     *
     * @param text      A decrypted text.
     * @param threshold Acceptance level of the text. Say, threshold is 2.
     *                  If any 2 or more words of the text is not English,
     *                  the whole text is not English.
     * @return {@code boolean} validation result.
     */
    protected boolean isEnglish(String text, int threshold) {
        String[] words = text.split("\\s+");
        Arrays.sort(words, new DescStringLengthComparator());

        boolean isEmpty = true;
        int counter = 0;

        for (String word : words) {
            if (dictionary.lookup(sanitize(word))) {
                isEmpty = false;
            } else if (++counter >= threshold) {
                return false;
            }
        }
        return !isEmpty;
    }

    /**
     * Calculates the threshold at which the program can decide that
     * the decrypted text is not English.
     *
     * @param text The text to calculate the threshold of.
     * @return Calculated threshold.
     */
    protected int calcThreshold(String text) {
        String[] words = text.split("\\s+");
        double percent;
        int len = countSafeWords(words);

        if (len <= 5) {
            percent = .9;
        } else if (len <= 30) {
            percent = .5;
        } else if (len <= 80) {
            percent = .4;
        } else if (len <= 130) {
            percent = .3;
        } else {
            percent = .2;
        }
        return (int) ((double) len * percent);
    }

    // ==============================
    // Private Methods
    // ==============================

    /**
     * Trims leading and trailing non-English-alphabet characters.
     *
     * @param s The word to trim.
     * @return The trimmed and clean word.
     */
    private String sanitize(String s) {
        s = s.replaceAll("^[^a-zA-Z]+", "");
        s = s.replaceAll("[^a-zA-Z]+$", "");
        return s;
    }

    /**
     * Counts the total number of distinguishable (safe) English words from the words array.
     *
     * <p>Given the text: i ispurz g pax bank bat i lokk stange
     * <br>Distinguishable are: strange, bank {@code >= 4}
     * <br>Non-distinguishable are:  i, g, bat {@code < 4}</p>
     *
     * @param words The words array to count the safe words from.
     * @return The total number of safe words.
     */
    private int countSafeWords(String[] words) {
        int safeLevel = 4;
        int total = 0;
        for (String w : words) {
            if (w.length() >= safeLevel) {
                total++;
            }
        }
        return total;
    }
}
