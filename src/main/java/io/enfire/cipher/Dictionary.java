package io.enfire.cipher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class creates an object which contains dictionary words.
 */
public class Dictionary {
    // ==============================
    // Fields
    // ==============================

    /**
     * 26 lower case English alphabet.
     */
    private String alphabet = "abcdefghijklmnopqrstuvwxyz";

    /**
     * A map to contain dictionary words.
     *
     * <p>{@code map{a={0=List...45=List}...z, *}}</p>
     *
     * <p>@implSpec A nested map. The outer map holds 26 rooms for English alphabet and
     * an extra room for non-alphabet characters (*). The inner one is grouped by lengths of words;
     * starting from 0 to 45 (longest English word has 45 letters). Room 0 is for words longer than 45.</p>
     *
     * <p>@implNote The reason for using {@link Map} is to loosely contain the words. For example,
     * the outer map might not have all 26 alphabet and other characters, and nor might the inner map.</p>
     */
    private Map<Character, Map<Integer, ArrayList<String>>> map = new HashMap<>();

    // ==============================
    // Constructors
    // ==============================

    /**
     * Creates a new {@code Dictionary} instance.
     *
     * @param file Dictionary file path.
     * @throws IOException if the file path does not exist.
     */
    public Dictionary(String file) throws IOException {
        Reader reader = new FileReader(file);
        BufferedReader buff = new BufferedReader(reader);

        String word;
        while ((word = buff.readLine()) != null) {
            if (word.isBlank())
                continue;

            word = word.toLowerCase().strip();

            char alphaKey = getOuterKey(word);
            Map<Integer, ArrayList<String>> alphaGroup = map.computeIfAbsent(alphaKey, k -> new HashMap<>());

            int lengthKey = getInnerKey(word);
            ArrayList<String> lengthGroup = alphaGroup.computeIfAbsent(lengthKey, k -> new ArrayList<>());

            lengthGroup.add(word);
        }

        buff.close();
        reader.close();
    }

    // ==============================
    // Public Methods
    // ==============================

    /**
     * Looks up the word in the dictionary map.
     *
     * @param word The word to look up.
     * @return {@code boolean} result of the lookup.
     */
    public boolean lookup(String word) {
        if (word.isBlank())
            return false;

        word = word.toLowerCase();

        char alphaKey = getOuterKey(word);
        Map<Integer, ArrayList<String>> alphaGroup = map.get(alphaKey);
        if (alphaGroup == null)
            return false;

        int lengthKey = getInnerKey(word);
        ArrayList<String> lengthGroup = alphaGroup.get(lengthKey);
        if (lengthGroup == null)
            return false;

        for (String dictWord : map.get(alphaKey).get(lengthKey)) {
            if (word.equals(dictWord)) {
                return true;
            }
        }
        return false;
    }

    // ==============================
    // Private Methods
    // ==============================

    /**
     * Gets the outer map key.
     *
     * @param word The word whose position is to find.
     * @return The outer map key.
     */
    private char getOuterKey(String word) {
        char firstLetter = word.charAt(0);
        return (alphabet.indexOf(firstLetter) == -1) ? '*' : firstLetter;
    }

    /**
     * Gets the inner map key.
     *
     * @param word The word whose position is to find.
     * @return The inner map key.
     */
    private int getInnerKey(String word) {
        int len = word.length();
        int longestEngWord = 45;
        return (len > longestEngWord) ? 0 : len;
    }
}