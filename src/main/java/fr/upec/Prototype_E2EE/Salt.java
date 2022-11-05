package fr.upec.Prototype_E2EE;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Salt {
    public static byte[] generate() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecureRandom secureRandom = new SecureRandom(keyGenerator.generateKey().getEncoded());
        return secureRandom.generateSeed(256 / 8);
    }
}
