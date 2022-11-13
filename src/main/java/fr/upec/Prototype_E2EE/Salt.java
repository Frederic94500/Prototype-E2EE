package fr.upec.Prototype_E2EE;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Salt is just a generator of random...
 */
public class Salt {
    /**
     * Generate a SecureRandom using AES(256)
     *
     * @return Return a SecureRandom
     */
    public static SecureRandom generate() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return new SecureRandom(keyGenerator.generateKey().getEncoded());

    }
}
