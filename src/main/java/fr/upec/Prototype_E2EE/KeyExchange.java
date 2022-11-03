package fr.upec.Prototype_E2EE;

import javax.crypto.KeyAgreement;
import java.security.*;

public class KeyExchange {
    public static Key createSharedKey(PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey);
        return keyAgreement.doPhase(publicKeyOther, true);
    }

    public static boolean compareSharedKeys(Key sharedKey, PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return sharedKey.equals(createSharedKey(publicKeyOther, privateKey));
    }
}
