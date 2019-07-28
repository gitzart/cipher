# Cipher
[![Build Status](https://travis-ci.org/gitzart/cipher.svg?branch=master)](https://travis-ci.org/gitzart/cipher)
[![codecov](https://codecov.io/gh/gitzart/cipher/branch/master/graph/badge.svg)](https://codecov.io/gh/gitzart/cipher)

Implementations of ciphers in Java for educational purpose.

## Example
```java
public class Main {
    public static void main(String[] args) throws IOException {
        // Encrypt
        String secret = new CaesarCipher(17).encrypt("Hello, Caesar!");
        System.out.println(secret);
        // => Yvccf, Trvjri!

        // Prepare decryption
        String file = "dictionary-book.txt"; 
        CaesarBreakerOneKey b = new CaesarBreakerOneKey(file);
        
        // Decrypt
        b.decrypt(secret);
        if (b.canDecrypt()) {
            System.out.println(b.key + " | " + b.decrypted);
            // => 17 | Hello, Caesar!
        } else {
            System.out.println("Can't decrypt");
        }
    }
}
```

## Links
- Dictionary: [http://app.aspell.net/create](http://app.aspell.net/create)
- Documentation: [https://gitzart.github.io/cipher](https://gitzart.github.io/cipher)
