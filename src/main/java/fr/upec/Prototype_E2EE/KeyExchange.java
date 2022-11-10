package fr.upec.Prototype_E2EE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class KeyExchange {
    public static byte[] HKDFExtract(byte[] salt, String ikm) throws NoSuchAlgorithmException {
        //String input_key = "Kono Dio Da!"; -> IKM
        Mac mac = Mac.getInstance("HmacSHA512");
        //mac.update(Salt.generate()); -> salt
        mac.update(salt);
        return mac.doFinal(ikm.getBytes(StandardCharsets.UTF_8));
    }

    //This part mainly come from the library HKDF by Patrick Favre-Bulle in GitHub
    public static byte[] HKDFExpand(byte[] prk, String info, int olb) throws NoSuchAlgorithmException, InvalidKeyException {
        if (olb <= 0 || prk == null) {
            throw new IllegalArgumentException();
        }

        //byte[] prk = HKDFExtract(); -> PRK
        SecretKey secretKey = new SecretKeySpec(prk, "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKey);

        //byte[] info = "ZA WARUDOOOOOOOOOOOOOO".getBytes(); -> info
        byte[] blockN = new byte[0];

        int iterations = (int) Math.ceil(((double) olb) / ((double) mac.getMacLength()));
        if (iterations > 255) {
            throw new IllegalArgumentException();
        }

        byte[] infoBytes = info.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(olb);
        int remainingBytes = olb;
        int stepSize;

        for (int i = 0; i < iterations; i++) {
            mac.update(blockN);
            mac.update(infoBytes);
            mac.update((byte) (i + 1));

            blockN = mac.doFinal();

            stepSize = Math.min(remainingBytes, blockN.length);

            buffer.put(blockN, 0, stepSize);
            remainingBytes -= stepSize;
        }

        return buffer.array();
    }

    public static String createMessage1(PublicKey publicKey, int salt) throws NoSuchAlgorithmException, InvalidKeyException {
        String output = "{\"pubkey\":" + publicKey +
                ",\"nonce\":" + salt +
                "\"timestamp\":" + (System.currentTimeMillis() / 1000L) +
                "}";
        return Tools.toBase64(output);
    }

    public static Key createSharedKey(PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey, new ECGenParameterSpec("secp384r1"));
        Key keyAgreed = keyAgreement.doPhase(publicKeyOther, true);

        byte[] hkdfExtract = HKDFExtract(keyAgreed.getEncoded(), "Kono Dio da!");
        byte[] hkdfExpand = HKDFExpand(hkdfExtract, "ZA WARUDOOOOOO", 256);

        return new SecretKeySpec(hkdfExpand, "AES");
    }

    public static SecretBuild handleMessage1(String message1) throws NoSuchAlgorithmException, IOException {
        Message1 otherMessage = new Gson().fromJson(message1, Message1.class);
        int myNonce = 1; //Need to check if nonce is superior to the old message and increment every new message
        String myPubKey = ""; //Need to retrieve my pub key
        String myPrivKey = ""; //Same as pub key
        PublicKey otherPubKey = new; //How to convert a Base64 to PubKey with DH...
        AlgorithmParameters ap = AlgorithmParameters.getInstance("DH");
        ap.init(Tools.toBytes(otherMessage.getPubKey()));
        ap.getEncoded();
        String symKey = Tools.toBase64(createSharedKey(, myPrivKey));
        return new SecretBuild((System.currentTimeMillis() / 1000L), otherMessage.getTimestamp(), myNonce, otherMessage.getNonce(), myPubKey, otherMessage.getPubKey(), )
    }

    public static String createMessage2(long myDate, long otherDate, int myNonce, int otherNonce, String myPubKey, String otherPubKey, String symKey) {
        Gson gson = new GsonBuilder().create();
        gson.toJson(new SecretBuild(myDate, otherDate, myNonce, otherNonce, myPubKey, otherPubKey, symKey));
        return Tools.toBase64(gson.toString());
    }

    public static boolean compareSharedKeys(Key sharedKey, PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        return sharedKey.equals(createSharedKey(publicKeyOther, privateKey));
    }
}
