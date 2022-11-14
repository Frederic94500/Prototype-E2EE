package fr.upec.Prototype_E2EE;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageCipher {

    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    public static byte[] cipher(byte[] plaintext, SecretKey key) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        // Create SecretKeySpec
       // SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        //GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);

        return cipherText;
    }
    public static byte[] decipher(byte[] cipherText, SecretKey key) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES_256/GCM/NoPadding");

        // Create SecretKeySpec
       // SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
       // GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Perform Decryption
        return cipher.doFinal(cipherText);
        //return new String(decryptedText);
    }
}
