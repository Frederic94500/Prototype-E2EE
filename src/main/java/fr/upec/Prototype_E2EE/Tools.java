package fr.upec.Prototype_E2EE;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.util.Arrays.copyOfRange;

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
     * Decode String Base64 to bytes
     *
     * @param in String Base64
     * @return Return Bytes
     */
    public static byte[] toBytes(String in) {
        return Base64.getDecoder().decode(in);
    }

    /**
     * Decode Bytes to PublicKey
     *
     * @param bytesPubKey Bytes Public Key
     * @return Return PublicKey
     */
    public static PublicKey toPublicKey(byte[] bytesPubKey) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bytesPubKey));
    }

    /**
     * Decode Bytes to PrivateKey
     *
     * @param privateKeyBytes Bytes Private Key
     * @return Return PrivateKey
     */
    public static PrivateKey toPrivateKey(byte[] privateKeyBytes) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    /**
     * Decode Bytes to SecretKey
     *
     * @param secretKeyBytes SecretKey in byte[]
     * @return Return a SecretKey
     */
    public static SecretKey toSecretKey(byte[] secretKeyBytes) {
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

    /**
     * Transform Bytes to Long from byte[]
     *
     * @param tab  byte[] source
     * @param from Start index
     * @param to   End index
     * @return Return a Long
     */
    public static Long toLong(byte[] tab, int from, int to) {
        ByteBuffer bb = ByteBuffer.wrap(copyOfRange(tab, from, to));
        return bb.getLong();
    }

    /**
     * Transform Bytes to int from byte[]
     *
     * @param tab  byte[] source
     * @param from Start index
     * @param to   End index
     * @return Return an int
     */
    public static int toInteger(byte[] tab, int from, int to) {
        ByteBuffer bb = ByteBuffer.wrap(copyOfRange(tab, from, to));
        return bb.getInt();
    }
}
