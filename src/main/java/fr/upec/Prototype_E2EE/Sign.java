package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class Sign {
    public static String sign(PrivateKey privateKey, String message) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA512withECDSAinP256Format"); //SHA512withECDSA
        signature.initSign(privateKey);

        byte[] messageByte = message.getBytes(StandardCharsets.UTF_8);
        signature.update(messageByte);

        byte[] messageSigned = signature.sign();

        return Base64.getEncoder().encodeToString(messageSigned);
    }

    public static Boolean verify(PublicKey publicKey, String signedMessage, String message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA512withECDSAinP256Format"); //SHA512withECDSA
        signature.initVerify(publicKey);

        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(Base64.getDecoder().decode(signedMessage));
    }
}
