package fr.upec.Prototype_E2EE;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Cipher dans Decipher text
 */
public class MessageCipher {
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

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

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, key);

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

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Perform Decryption and Return
        return cipher.doFinal(cipherText);
    }
    //yes
}
