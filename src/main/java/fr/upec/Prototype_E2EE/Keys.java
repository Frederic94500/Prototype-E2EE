package fr.upec.Prototype_E2EE;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;

/**
 * Generate Key Pair
 */
public class Keys {
    /**
     * Generate a KeyPair with a SecureRandom
     *
     * @return Return KeyPair
     */
    public static KeyPair generate() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"), Tools.generateSecureRandom()); //Not secp256k1
        return keyPairGenerator.generateKeyPair();
    }
}
