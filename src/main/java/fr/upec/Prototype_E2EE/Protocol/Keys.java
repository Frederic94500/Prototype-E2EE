package fr.upec.Prototype_E2EE.Protocol;

import fr.upec.Prototype_E2EE.Tools;

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
     * @throws NoSuchAlgorithmException           Throws NoSuchAlgorithmException if there is not the expected algorithm
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException if there is an invalid or inappropriate algorithm parameter
     */
    public static KeyPair generate() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"), Tools.generateSecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
