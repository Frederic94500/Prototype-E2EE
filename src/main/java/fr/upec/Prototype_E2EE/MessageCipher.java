package fr.upec.Prototype_E2EE;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.GeneralSecurityException;

import static java.util.Arrays.copyOfRange;

/**
 * Cipher and Decipher text
 */
public class MessageCipher {
    private static final int GCM_TAG_LENGTH = 12;

    /**
     * Cipher a text
     *
     * @param key  Symmetric Key
     * @param text Text in Bytes
     * @return Return a ciphered text in Bytes
     */
    public static byte[] cipher(SecretKey key, byte[] text) throws GeneralSecurityException {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        byte[] iv = copyOfRange(text, 0, 12);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        return cipher.doFinal(text);
    }

    /**
     * Decipher a ciphered text
     *
     * @param key     Symmetric Key
     * @param message Ciphered Text in Bytes
     * @return Return a Text in Bytes
     */
    public static byte[] decipher(SecretKey key, byte[] message) throws GeneralSecurityException {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        byte[] iv = copyOfRange(message, 0, 12);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        // Perform Decryption and Return
        return cipher.doFinal(message);
    }
    //yes
}
