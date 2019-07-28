package io.enfire.cipher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CaesarBreakerOneKeyTest {
    static CaesarBreakerOneKey b;

    @BeforeAll
    static void setUp() {
        String file = new File("").getAbsolutePath() + "/src/main/resources/dictionary.txt";
        try {
            b = new CaesarBreakerOneKey(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void canDecrypt() {
        b.decrypted = "a";
        b.key = 1;
        assertTrue(b.canDecrypt());

        b.decrypted = "  ";
        b.key = 1;
        assertFalse(b.canDecrypt());

        b.decrypted = "a";
        b.key = -1;
        assertFalse(b.canDecrypt());
    }

    @Test
    void decrypt_frequencyAnalysis() {
        int key = 7;
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
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertEquals(msg, b.decrypted);
        assertEquals(key, b.key);

        msg = "x1y ".repeat(51);
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertEquals("", b.decrypted);
        assertEquals(-1, b.key);
    }

    @Test
    void decrypt_bruteForce() {
        int key = 17;
        String msg;

        msg = "I me my mine myself.";
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertEquals(msg, b.decrypted);
        assertEquals(key, b.key);

        msg = "I me my mi1ne.";
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertEquals("", b.decrypted);
        assertEquals(-1, b.key);
    }

    @Test
    void decrypt_edgeCase() {
        int key = 7;
        String msg;

        msg = "  ";
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertEquals("", b.decrypted);
        assertEquals(-1, b.key);

        // Output of "I me my" encrypted with key 7:
        // decrypted = "Q um ug" | key = 25
        //
        // In this case, decryption fails. Not because it's not correct,
        // but the length of each word (I, me, my) is way too small to guess.
        // Also, (q, um, ug) are valid English words in the given dictionary.
        // Thus, correctness of algorithm relies on both word length and
        // the type of dictionary.
        //
        msg = "I me my";
        b.decrypt(new CaesarCipher(key).encrypt(msg));
        assertTrue(b.canDecrypt());
        assertNotEquals(msg, b.decrypted);
        assertEquals("Q um ug", b.decrypted);
        assertEquals(25, b.key);
    }
}