package fr.upec.Prototype_E2EE.Protocol;

import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Cipher and Decipher text
 */
public class Cipher {
    /**
     * Galois Counter Mode IV
     */
    public static final int GCM_IV_LENGTH = 12;
    /**
     * Galois Counter Mode Tag
     */
    public static final int GCM_TAG_LENGTH = 16;
    /**
     * PBKDF2 Number of iteration
     */
    public static final int PBKDF2_ITERATION = 1048576;

    /**
     * Cipher a text
     *
     * @param secretKey Symmetric Key
     * @param text      Text in Bytes
     * @return Return a ciphered text in Bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] cipher(SecretKey secretKey, byte[] text) throws GeneralSecurityException {
        // Get Cipher Instance
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES_256/GCM/NoPadding");

        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = Tools.generateSecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] cipherText = cipher.doFinal(text);

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    /**
     * Decipher a ciphered text
     *
     * @param secretKey     Symmetric Key
     * @param cipherMessage Ciphered Text in Bytes
     * @return Return a Text in Bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] decipher(SecretKey secretKey, byte[] cipherMessage) throws GeneralSecurityException {
        // Get Cipher Instance
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES_256/GCM/NoPadding");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, cipherMessage, 0, GCM_IV_LENGTH);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        // Perform Decryption and Return
        return cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);
    }

    /**
     * Cipher bytes with PBKDF2
     *
     * @param password Password
     * @param input    Input to cipher
     * @return Return ciphered bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] cipherPBKDF2(char[] password, byte[] input) throws GeneralSecurityException {
        byte[] salt = Tools.generateSecureRandom().generateSeed(32);
        byte[] iv = Tools.generateSecureRandom().generateSeed(GCM_IV_LENGTH);

        SecretKey secretKey = getSecretKeyPBKDF2(password, salt);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES_256/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] cipherText = cipher.doFinal(input);

        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        return byteBuffer.put(salt).put(iv).put(cipherText).array();
    }

    /**
     * Decipher bytes with PBKDF2
     *
     * @param password Password
     * @param input    Input to decipher
     * @return Return deciphered bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] decipherPBKDF2(char[] password, byte[] input) throws GeneralSecurityException {
        byte[] salt = Arrays.copyOfRange(input, 0, 32);
        byte[] cipherText = Arrays.copyOfRange(input, 32, input.length);

        SecretKey secretKey = getSecretKeyPBKDF2(password, salt);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES_256/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, cipherText, 0, GCM_IV_LENGTH);

        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return cipher.doFinal(cipherText, GCM_IV_LENGTH, cipherText.length - GCM_IV_LENGTH);
    }

    /**
     * Get Secret Key with PBKDF2
     *
     * @param password Password
     * @param salt     Salt
     * @return Return SecretKey AES
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private static SecretKey getSecretKeyPBKDF2(char[] password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, PBKDF2_ITERATION, 256);
        SecretKey temp = keyFactory.generateSecret(pbeKeySpec);
        return new SecretKeySpec(temp.getEncoded(), "AES");
    }
}
