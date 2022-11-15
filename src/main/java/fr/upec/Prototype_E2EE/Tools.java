package fr.upec.Prototype_E2EE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
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
     * Decode String Base64 to String
     *
     * @param in String Base64
     * @return Return String
     */
    public static String base64ToString(String in) {
        return new String(toBytes(in));
    }

    /**
     * Retrieve Public Key with Base64 encoding
     *
     * @param base64String Public Key as Base64 encoding
     * @return Return Public Key
     */
    public static PublicKey getPublicKey(String base64String) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] pubBytes = Tools.toBytes(base64String);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * Serializes Object to JSON
     *
     * @param o Object
     * @return Return String JSON
     */
    public static String toJSON(Object o) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(o);
    }

    /**
     * Decode Bytes to PublicKey
     *
     * @param bytesPubKey Bytes Public Key
     * @return Return PublicKey
     */
    public static PublicKey toPublicKey(byte[] bytesPubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bytesPubKey));
    }

    /**
     * Decode Base64 to PublicKey
     *
     * @param base64PubKey Base64 Public Key
     * @return Return PublicKey
     */
    public static PublicKey toPublicKey(String base64PubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return toPublicKey(toBytes(base64PubKey));
    }

    /**
     * Decode Base64 to SecretKey
     *
     * @param base64SecretKey Base64 Secret Key (Symmetric Key)
     * @return Return SecretKey
     */
    public static SecretKey toSecretKey(String base64SecretKey) {
        byte[] secretKeyBytes = toBytes(base64SecretKey);
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    /**
     * Generate a SecureRandom using AES(256)
     *
     * @return Return a SecureRandom
     */
    public static SecureRandom generateSecureRandom() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return new SecureRandom(keyGenerator.generateKey().getEncoded());

    }
}
