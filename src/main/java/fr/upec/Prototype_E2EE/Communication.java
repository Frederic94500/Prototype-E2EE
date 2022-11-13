package fr.upec.Prototype_E2EE;

import com.google.gson.Gson;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static fr.upec.Prototype_E2EE.Tools.*;

/**
 * Create and Handle Messages
 */
public class Communication {
    /**
     * Create message 1 for the key negotiation/agreement
     * Message1 -> JSON -> Base64
     *
     * @param publicKey Your Public Key
     * @param salt      A salt number, a counter of message
     * @return Return the message 1 as Base64
     */
    public static String createMessage1(PublicKey publicKey, int salt) {
        Message1 message1 = new Message1(toBase64(publicKey.getEncoded()), salt, System.currentTimeMillis() / 1000L);
        return toBase64(toJSON(message1));
    }

    /**
     * Handle the message 1 received from other
     * otherMessage1 (Base64) -> JSON -> Message1
     *
     * @param otherMessage1 Message 1 received from other
     * @return Return a SecureBuild
     */
    public static SecretBuild handleMessage1(String otherMessage1) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        String otherMessage1JSON = base64ToString(otherMessage1);
        Message1 otherMessage = new Gson().fromJson(otherMessage1JSON, Message1.class);

        int myNonce = 1; //Need to check if nonce is superior to the old message and increment every new message
        String myPubKey = ""; //Need to retrieve my pub key
        PrivateKey myPrivKey = ""; //Same as pub key
        PublicKey otherPubKey = getPublicKey(otherMessage.getPubKey());
        String symKey = toBase64(KeyExchange.createSharedKey(otherPubKey, myPrivKey).getEncoded());

        return new SecretBuild((System.currentTimeMillis() / 1000L),
                otherMessage.getTimestamp(),
                myNonce,
                otherMessage.getNonce(),
                myPubKey,
                otherMessage.getPubKey(),
                symKey);
    }

    /**
     * Create message 2 by signing then ciphering
     * SecretBuild -> JSON -> Signed (Bytes) -> Ciphered (Bytes) -> Base64
     *
     * @param privateKey  Your Private Key
     * @param secretBuild Your SecretBuild
     * @return Return the signed and ciphered message 2 as Base64
     */
    public static String createMessage2(PrivateKey privateKey, SecretBuild secretBuild) throws InvalidAlgorithmParameterException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message2 = toJSON(secretBuild);

        //Need to have an ID verification in Android
        byte[] signedMessage = Sign.sign(privateKey, message2);
        byte[] cipheredSignedMessage = MessageCipher.cipher(signedMessage);

        return toBase64(cipheredSignedMessage);
    }

    /**
     * Handle the message 2 received from other
     * otherMessage2 (Base64) -> Bytes -> Deciphered (Bytes) -> Signed (Bytes)
     *
     * @param otherMessage2 Message 2 received by other
     * @param mySecretBuild Your SecretBuild
     * @return Return a boolean if the message 2 is authentic
     */
    public static Boolean handleMessage2(String otherMessage2, SecretBuild mySecretBuild) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {
        SecretBuild otherSecretBuild = new SecretBuild(mySecretBuild);
        String otherSecretBuildJSON = toJSON(otherSecretBuild);
        String otherSecretBuildBase64 = toBase64(otherSecretBuildJSON);

        byte[] cipheredSignedOtherMessage2 = toBytes(otherMessage2);
        byte[] signedMessage = MessageCipher.decipher(cipheredSignedOtherMessage2);

        return Sign.verify(toPublicKey(mySecretBuild.getOtherPubKey()), signedMessage, otherSecretBuildBase64);
    }
}
