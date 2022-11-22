package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Sign for signing message
 */
public class Sign {
    /**
     * Sign a message using SHA512-ECDSA
     *
     * @param privateKey Your Private Key
     * @param message    Your Message
     * @return Return a signed message
     */
    public static byte[] sign(PrivateKey privateKey, String message) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        //Need to have an ID verification in Android
        signature.initSign(privateKey);

        byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);
        signature.update(messageByte);

        return signature.sign();
    }

    /**
     * Verify a signed message using SHA512-ECDSA
     *
     * @param publicKey     Other Public Key
     * @param signedMessage The signed message
     * @param message       The message
     * @return Return a boolean if the message come from the other
     */
    public static Boolean verify(PublicKey publicKey, byte[] signedMessage, String message) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.initVerify(publicKey);

        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signedMessage);
    }
}
