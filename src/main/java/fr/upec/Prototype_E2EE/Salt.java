package fr.upec.Prototype_E2EE;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Salt { //unsure
    public static String generate() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecureRandom secureRandom = new SecureRandom(keyGenerator.generateKey().getEncoded());

        Mac mac = Mac.getInstance("HmacSHA512");
        return Base64.getEncoder().encodeToString(mac.doFinal(secureRandom.generateSeed(32)));
    }
}
