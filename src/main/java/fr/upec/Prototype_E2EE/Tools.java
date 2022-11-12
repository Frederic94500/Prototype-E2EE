package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Some tools...
 */
public class Tools {
    /**
     * Encode bytes to String Base64
     *
     * @param in Bytes
     * @return Return String as Base64
     */
    public static String toBase64(byte[] in) {
        return Base64.getEncoder().encodeToString(in);
    }

    /**
     * Encode String to String Base64
     *
     * @param in String
     * @return Return String Base64
     */
    public static String toBase64(String in) {
        return Base64.getEncoder().encodeToString(in.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode String Base64 to bytes
     *
     * @param in String Base64
     * @return Return Bytes
     */
    public static byte[] toBytes(String in) {
        return Base64.getDecoder().decode(in);
    }

    /**
     * Retrieve Public Key with Base64 encoding
     *
     * @param base64String Public Key as Base64 encoding
     * @return Return Public Key
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PublicKey getPublicKey(String base64String) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] pubBytes = Tools.toBytes(base64String);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }
}
