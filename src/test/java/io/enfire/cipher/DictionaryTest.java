package io.enfire.cipher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryTest {
    private static Dictionary d;

    @BeforeAll
    static void setUp() {
        String file = new File("").getAbsolutePath() + "/src/testFile.txt";
        String s = "ça va\n" +
                "   damp\n" +
                "   demon\n" +
                "   \n" +
                "   earth\n" +
                "   EARTHY\n";
        s += "long".repeat(12);

        try {
            FileWriter w = new FileWriter(file);
            w.write(s);
            w.close();
            d = new Dictionary(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(file);
        System.out.println(f.delete() ? "Deleted file" : "Failed to delete");
    }

    @Test
    void constructor_throws() {
        String file = "doesNotExist.txt";
        assertThrows(IOException.class, () -> new Dictionary(file));
    }

    @Test
    void lookup() {
        assertFalse(d.lookup(""));
        assertFalse(d.lookup("damps"));

        assertTrue(d.lookup("DEMON"));
        assertTrue(d.lookup("earthy"));
    }

    @Test
    void lookup_notExistingAlphabet() {
        assertFalse(d.lookup("apple"));
    }

    @Test
    void lookup_notExistingLength() {
        assertFalse(d.lookup("damping"));
    }

    @Test
    void lookup_nonAlphabet() {
        assertTrue(d.lookup("ça va"));
    }

    @Test
    void lookup_longerThanMax() {
        assertTrue(d.lookup("long".repeat(12)));
    }
}