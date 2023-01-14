package fr.upec.Prototype_E2EE.Protocol;

import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * Cipher and Decipher text
 */
public class MessageCipher {
    /**
     * Galois Counter Mode IV
     */
    public static final int GCM_IV_LENGTH = 12;
    /**
     * Galois Counter Mode Tag
     */
    public static final int GCM_TAG_LENGTH = 16;

    /**
     * Cipher a text
     *
     * @param key  Symmetric Key
     * @param text Text in Bytes
     * @return Return a ciphered text in Bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] cipher(SecretKey key, byte[] text) throws GeneralSecurityException {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = Tools.generateSecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        byte[] cipherText = cipher.doFinal(text);

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    /**
     * Decipher a ciphered text
     *
     * @param key           Symmetric Key
     * @param cipherMessage Ciphered Text in Bytes
     * @return Return a Text in Bytes
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static byte[] decipher(SecretKey key, byte[] cipherMessage) throws GeneralSecurityException {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, cipherMessage, 0, GCM_IV_LENGTH);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        // Perform Decryption and Return
        return cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);
    }
}
