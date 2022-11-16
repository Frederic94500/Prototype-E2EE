package fr.upec.Prototype_E2EE;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static fr.upec.Prototype_E2EE.Tools.*;
import static java.util.Arrays.copyOfRange;

/**
 * Create and Handle Messages
 */
public class Communication {
    /**
     * Create message 1 for the key negotiation/agreement
     * Message1 -> Base64
     *
     * @param publicKey Your Public Key
     * @param salt      A salt number, a counter of message
     * @return Return the message 1 as Base64
     */
    public static String createMessage1(PublicKey publicKey, int salt) {
        Message1 message1 = new Message1(System.currentTimeMillis() / 1000L, salt, publicKey.getEncoded());
        return toBase64(message1.toBytes());
    }

    public static String createMessage1(Message1 message1) {
        return toBase64(message1.toBytes());
    }

    /**
     * Handle the message 1 received from other
     * otherMessage1 (Base64) -> Message1
     *
     * @param otherMessage1 Message 1 received from other
     * @return Return a SecureBuild
     */
    public static SecretBuild handleMessage1(KeyPair myKeyPair, Message1 myMessage1, String otherMessage1) throws GeneralSecurityException {
        byte[] otherMessage1Bytes = toBytes(otherMessage1);

        //int myNonce = 1; //Need to check if nonce is superior to the old message and increment every new message
        long otherTimestamp = toLong(otherMessage1Bytes, 0, 8);
        int otherNonce = toInteger(otherMessage1Bytes, 8, 12);
        byte[] otherPubKeyByte = copyOfRange(otherMessage1Bytes, 12, 103);

        //Need to retrieve my pub key
        PrivateKey myPrivKey = myKeyPair.getPrivate(); //Same as pub key -> UNSAFE
        PublicKey otherPubKey = toPublicKey(otherPubKeyByte);
        byte[] symKey = KeyExchange.createSharedKey(otherPubKey, myPrivKey, myMessage1.getNonce(), otherNonce, "Shinzou o Sasageyo!").getEncoded();

        return new SecretBuild((System.currentTimeMillis() / 1000L),
                otherTimestamp,
                myMessage1.getNonce(),
                otherNonce,
                myKeyPair.getPublic().getEncoded(),
                otherPubKeyByte,
                symKey);
    }

    /**
     * Create message 2 by signing then ciphering
     * SecretBuild -> Signed (Bytes) -> Ciphered (Bytes) -> Base64
     *
     * @param myPrivateKey  Your Private Key
     * @param mySecretBuild Your SecretBuild
     * @return Return the signed and ciphered message 2 as Base64
     */
    public static String createMessage2(PrivateKey myPrivateKey, SecretBuild mySecretBuild) throws Exception {
        String message2Base64 = toBase64(mySecretBuild.toBytesWithoutSymKey());

        byte[] signedMessage = Sign.sign(myPrivateKey, message2Base64);
        byte[] cipheredSignedMessage = MessageCipher.cipher(toSecretKey(mySecretBuild.getSymKey()), signedMessage);

        return toBase64(cipheredSignedMessage);
    }

    /**
     * Handle the message 2 received from other
     * otherMessage2 (Base64) -> Bytes -> Deciphered (Bytes) -> Signed (Bytes)
     *
     * @param mySecretBuild Your SecretBuild
     * @param otherMessage2 Message 2 received by other
     * @return Return a boolean if the message 2 is authentic
     */
    public static Boolean handleMessage2(SecretBuild mySecretBuild, String otherMessage2) throws Exception {
        SecretBuild otherSecretBuild = new SecretBuild(mySecretBuild);
        byte[] otherSecretBuildBytes = otherSecretBuild.toBytesWithoutSymKey();

        byte[] cipheredSignedOtherMessage2 = toBytes(otherMessage2);
        byte[] signedMessage = MessageCipher.decipher(toSecretKey(mySecretBuild.getSymKey()), cipheredSignedOtherMessage2);

        return Sign.verify(toPublicKey(mySecretBuild.getOtherPubKey()), signedMessage, toBase64(otherSecretBuildBytes));
    }
}
