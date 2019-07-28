package io.enfire.cipher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CaesarBreakerTwoKeyTest {
    static CaesarBreakerTwoKey b;

    @BeforeAll
    static void setUp() {
        String file = new File("").getAbsolutePath() + "/src/main/resources/dictionary.txt";
        try {
            b = new CaesarBreakerTwoKey(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void canDecrypt() {
        b.decrypted = "a"; // all good
        b.key[0] = 1;
        b.key[1] = 2;
        assertTrue(b.canDecrypt());

        b.decrypted = "  "; // no message
        b.key[0] = 1;
        b.key[1] = 2;
        assertFalse(b.canDecrypt());

        b.decrypted = "a"; // key 1 < 0
        b.key[0] = -1;
        b.key[1] = 2;
        assertFalse(b.canDecrypt());

        b.decrypted = "a"; // key2 < 0
        b.key[0] = 2;
        b.key[1] = -1;
        assertFalse(b.canDecrypt());
    }

    @Test
    void decrypt_frequencyAnalysis() {
        int[] key = new int[]{7, 17};
        String msg;

        // An excerpt from Romeo and Juliet (59 words).
        msg = "Prince. Rebellious subjects, enemies to peace,\n" +
                "    Profaners of this neighbour-stained steel-\n" +
                "    Will they not hear? What, ho! you men, you beasts,\n" +
                "    That quench the fire of your pernicious rage\n" +
                "    With purple fountains issuing from your veins!\n" +
                "    On pain of torture, from those bloody hands\n" +
                "    Throw your mistempered weapons to the ground\n" +
                "    And hear the sentence of your moved prince.";
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertEquals(msg, b.decrypted);
        assertArrayEquals(key, b.key);

        msg = "x1y ".repeat(51);
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertEquals("", b.decrypted);
        assertArrayEquals(new int[]{-1, -1}, b.key);
    }

    @Test
    void decrypt_bruteForce() {
        int[] key = new int[]{17, 1};
        String msg;

        msg = "I me my mine myself.";
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertEquals(msg, b.decrypted);
        assertArrayEquals(key, b.key);

        msg = "I me my mi1ne.";
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertEquals("", b.decrypted);
        assertArrayEquals(new int[]{-1, -1}, b.key);
    }

    @Test
    void decrypt_edgeCase() {
        int[] key = new int[]{7, 17};
        String msg;

        msg = "  ";
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertEquals("", b.decrypted);
        assertArrayEquals(new int[]{-1, -1}, b.key);

        // Output of "I me my mine." encrypted with keys 7 and 17:
        // decrypted = "H le mx lime." | key = 8, 17
        //
        // In this case, decryption fails. Not because it's not correct,
        // but the length of each word (I, me, my, mine.) is way too small to guess.
        // Also, (h, le, mx, lime) are valid English words in the given dictionary.
        // Thus, correctness of algorithm relies on both word length and
        // the type of dictionary.
        //
        msg = "I me my mine.";
        b.decrypt(new CaesarCipher(key[0], key[1]).encrypt(msg));
        assertTrue(b.canDecrypt());
        assertNotEquals(msg, b.decrypted);
        assertEquals("H le mx lime.", b.decrypted);
        assertArrayEquals(new int[]{8, 17}, b.key);
    }
}