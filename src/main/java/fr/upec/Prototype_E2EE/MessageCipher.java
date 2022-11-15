package fr.upec.Prototype_E2EE;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Cipher and Decipher text
 */
public class MessageCipher {
    private static final int GCM_TAG_LENGTH = 12;
    private static final byte[] IV = {26, 93, 37, -30, 108, 64, -92, 23, -51, -112, 17, -119};

    /**
     * Cipher a text
     *
     * @param key  Symmetric Key
     * @param text Text in Bytes
     * @return Return a ciphered text in Bytes
     */
    public static byte[] cipher(SecretKey key, byte[] text) throws Exception {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        return cipher.doFinal(text);
    }

    /**
     * Decipher a ciphered text
     *
     * @param key        Symmetric Key
     * @param cipherText Ciphered Text in Bytes
     * @return Return a Text in Bytes
     */
    public static byte[] decipher(SecretKey key, byte[] cipherText) throws Exception {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        // Perform Decryption and Return
        return cipher.doFinal(cipherText);
    }
    //yes
}
