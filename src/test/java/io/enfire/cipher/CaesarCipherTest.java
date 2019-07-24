package io.enfire.cipher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CaesarCipherTest {
    @Test
    void constructor_oneKeyThrow() {
        assertThrows(KeyOutOfBoundsException.class, () -> new CaesarCipher(-1));
        assertThrows(KeyOutOfBoundsException.class, () -> new CaesarCipher(27));
    }

    @Test
    void constructor_twoKeyThrow() {
        assertThrows(KeyOutOfBoundsException.class, () -> new CaesarCipher(1, -1));
        assertThrows(KeyOutOfBoundsException.class, () -> new CaesarCipher(27, 26));
    }

    @Test
    void encrypt_oneKey() {
        String msg = "xyz";
        assertEquals("", new CaesarCipher(0).encrypt("   "));
        assertEquals("xyz", new CaesarCipher(0).encrypt(msg));
        assertEquals("zab", new CaesarCipher(2).encrypt(msg));
    }

    @Test
    void encrypt_twoKey() {
        String msg = "abc xyz";
        assertEquals("mho jel", new CaesarCipher(12, 6).encrypt(msg));
        assertEquals("irk foh", new CaesarCipher(8, 16).encrypt(msg));
    }

    @Test
    void encrypt_mixedCharacters() {
        String msg = "he says: пить молоко";
        assertEquals("qn bjhb: пить молоко", new CaesarCipher(9).encrypt(msg));
        assertEquals("mz nftx: пить молоко", new CaesarCipher(5, 21).encrypt(msg));
    }

    @Test
    void encrypt_maintainCase() {
        assertEquals("Lm!", new CaesarCipher(4).encrypt("Hi!"));
        assertEquals("Ab Ba", new CaesarCipher(12, 13).encrypt("Oo Oo"));
    }
}