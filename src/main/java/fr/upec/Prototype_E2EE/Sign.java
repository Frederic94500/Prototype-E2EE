package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Sign for signing message
 */
public class Sign {
    /**
     * Sign a message using SHA512-ECDSA(secp256k1)
     *
     * @param privateKey Your Private Key
     * @param message    Your Message
     * @return Return a signed message
     */
    public static byte[] sign(PrivateKey privateKey, String message) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        //signature.setParameter(new ECGenParameterSpec("secp256k1")); //No Parameter possible
        signature.initSign(privateKey);

        byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);
        signature.update(messageByte);

        return signature.sign();
    }

    /**
     * Verify a signed message using SHA512-ECDSA(secp256k1)
     *
     * @param publicKey     Other Public Key
     * @param signedMessage The signed message
     * @param message       The message
     * @return Return a boolean if the message come from the other
     */
    public static Boolean verify(PublicKey publicKey, byte[] signedMessage, String message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        //signature.setParameter(new ECGenParameterSpec("secp256k1")); //No Parameter possible
        signature.initVerify(publicKey);

        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signedMessage);
    }
}
