package fr.upec.Prototype_E2EE;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class KeyExchange {
    //This part principally come from the library HKDF by Patrick Favre-Bulle in GitHub
    public static byte[] HKDFExtract() throws NoSuchAlgorithmException {
        String input_key = "Kono Dio Da!";
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.update(Salt.generate());
        return mac.doFinal(input_key.getBytes());
    }

    public static byte[] HKDFExpand() throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] prk = HKDFExtract();
        SecretKey secretKey = new SecretKeySpec(prk, "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKey);

        byte[] info = "ZA WARUDOOOOOOOOOOOOOO".getBytes();
        byte[] blockN = new byte[0];
        int iterations = (int) Math.ceil(((double) 16384) / ((double) mac.getMacLength()));
        ByteBuffer buffer = ByteBuffer.allocate(16384);
        int remainingBytes = 16384;
        int stepsize;

        for (int i = 0; i < iterations; i++) {
            mac.update(blockN);
            mac.update(info);
            mac.update((byte) (i + 1));

            blockN = mac.doFinal();

            stepsize = Math.min(remainingBytes, blockN.length);

            buffer.put(blockN, 0, stepsize);
            remainingBytes -= stepsize;
        }

        return buffer.array();
        /*MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] digest = messageDigest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toHexString(0xFF & b));
        }
        return sb.toString();*/
    }

    public static Key createSharedKey(PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH"); //missing HKDF(SHA512)
        keyAgreement.init(privateKey, new ECGenParameterSpec("secp384r1"));
        return keyAgreement.doPhase(publicKeyOther, true);
    }

    public static boolean compareSharedKeys(Key sharedKey, PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        return sharedKey.equals(createSharedKey(publicKeyOther, privateKey));
    }
}
