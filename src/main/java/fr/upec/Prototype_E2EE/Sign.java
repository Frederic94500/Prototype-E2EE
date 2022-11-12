package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

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
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static byte[] sign(PrivateKey privateKey, String message) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.setParameter(new ECGenParameterSpec("secp256k1"));
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
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public static Boolean verify(PublicKey publicKey, byte[] signedMessage, String message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.setParameter(new ECGenParameterSpec("secp256k1"));
        signature.initVerify(publicKey);

        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signedMessage);
    }
}
