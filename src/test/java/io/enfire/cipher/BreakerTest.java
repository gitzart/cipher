package io.enfire.cipher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BreakerTest {
    private static AbstractBreaker ab;

    @BeforeAll
    static void setUp() {
        String file = new File("").getAbsolutePath() + "/src/main/resources/dictionary.txt";
        try {
            ab = new AbstractBreaker(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void read_File() {
        String file = new File("").getAbsolutePath() + "/src/testFile.txt";
        String s = "apple\nbanana\ncherry";

        try {
            FileWriter w = new FileWriter(file);
            w.write(s);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(s, ab.read(file));
        File f = new File(file);
        System.out.println(f.delete() ? "Deleted file" : "Failed to delete");
    }

    @Test
    void read_Text() {
        String s = "test string";
        assertEquals(s, ab.read(s));
    }

    @Test
    void count_emptyText() {
        int[] counter = ab.count("");
        assertArrayEquals(new int[26], counter);
    }

    @Test
    void count_alphabet() {
        int[] counter = ab.count("AaaBbz");
        int[] expected = new int[26];
        expected[0] = 3;
        expected[1] = 2;
        expected[25] = 1;
        assertArrayEquals(expected, counter);
    }

    @Test
    void count_nonAlphabet() {
        int[] counter = ab.count("àç123!@#");
        assertArrayEquals(new int[26], counter);
    }

    @Test
    void getMaxCount() {
        int[] counter = new int[]{6, 0, 5, 2, 7};
        assertEquals(4, ab.getMaxCount(counter));
    }

    @Test
    void calcKey() {
        // most frequent letter of counter array
        int p = 15;
        // one of frequent letters
        int v = 21;
        int g = 6;

        assertEquals(6, ab.calcKey(p, v));
        assertEquals(17, ab.calcKey(p, g));
        assertEquals(26, ab.calcKey(p, p));
    }

    @Test
    void isEnglish() {
        assertTrue(ab.isEnglish("i me my mine", 0));
        assertTrue(ab.isEnglish("i me my mine", 1));

        assertFalse(ab.isEnglish("i me my mineeeee", 0));
        assertFalse(ab.isEnglish("i me my mineeeee", 1));
    }

    @Test
    void isEnglish_edgeCase() {
        // 1minus2 after trimming is "minus".
        assertTrue(ab.isEnglish("1minus2", 0));

        // H1N1 [virus], even after trimming, is not a proper English.
        assertFalse(ab.isEnglish("H1N1", 2));

        assertFalse(ab.isEnglish("", 0));
    }

    @Test
    void calcThreshold() {
        String s;

        s = "";
        assertEquals(0, ab.calcThreshold(s));

        // 90% check | 3 safe words
        s = "rainy sunshine season wet car";
        assertEquals(2, ab.calcThreshold(s));

        // 50% check | 10 safe words
        s = "rainy sunshine season house paint wet car ";
        assertEquals(5, ab.calcThreshold(s.repeat(2)));

        // 40% check | 40 safe words
        assertEquals(16, ab.calcThreshold(s.repeat(8)));

        // 30% check | 90 safe words
        assertEquals(27, ab.calcThreshold(s.repeat(18)));

        // 20% check | 140 safe words
        assertEquals(28, ab.calcThreshold(s.repeat(28)));
    }

    private static class AbstractBreaker extends Breaker {
        public AbstractBreaker(String file) throws IOException {
            super(file);
        }

        public boolean canDecrypt() {
            return false;
        }

        public void decrypt(String s) {
        }
    }
}