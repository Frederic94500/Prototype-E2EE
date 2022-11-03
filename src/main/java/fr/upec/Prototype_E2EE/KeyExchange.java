package fr.upec.Prototype_E2EE;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class KeyExchange {
    public static Key createSharedKey(PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH"); //missing HKDF(SHA512)
        keyAgreement.init(privateKey, new ECGenParameterSpec("secp384r1"));
        return keyAgreement.doPhase(publicKeyOther, true);
    }

    public static boolean compareSharedKeys(Key sharedKey, PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        return sharedKey.equals(createSharedKey(publicKeyOther, privateKey));
    }
}
