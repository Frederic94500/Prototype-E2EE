package fr.upec.Prototype_E2EE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

/**
 * KeyExchange for the key negotiation/agreement
 */
public class KeyExchange {
    /**
     * HMAC Key Derivation Function (HKDF) Extractor
     * See <a href="https://www.rfc-editor.org/rfc/rfc5869">RFC5869</a>
     *
     * @param salt Salt value (a non-secret random value)
     * @param ikm  Input Keying Material
     * @return Return a PseudoRandom Key
     * @throws NoSuchAlgorithmException If HmacSHA512 doesn't exist...
     */
    public static byte[] HKDFExtract(byte[] salt, String ikm) throws NoSuchAlgorithmException {
        //String input_key = "Kono Dio Da!"; -> IKM
        Mac mac = Mac.getInstance("HmacSHA512");
        //mac.update(Salt.generate()); -> salt
        mac.update(salt);
        return mac.doFinal(ikm.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * HMAC Key Derivation Function (HKDF) Expand
     * This part mainly come from the library HKDF by Patrick Favre-Bulle in <a href="https://github.com/patrickfav/hkdf">GitHub</a>
     * See <a href="https://www.rfc-editor.org/rfc/rfc5869">RFC5869</a>
     *
     * @param prk  PseudoRandom Key
     * @param info An information
     * @param olb  Out Length Bytes
     * @return Return a HKDF
     * @throws NoSuchAlgorithmException If HmacSHA512 doesn't exist...
     * @throws InvalidKeyException      If the key is incorrect
     */
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

    /**
     * Create message 1 for the key negotiation/agreement
     *
     * @param publicKey Your Public Key
     * @param salt      A salt number, a counter of message
     * @return Return the message 1 as Base64
     */
    public static String createMessage1(PublicKey publicKey, int salt) {
        String output = "{\"pubkey\":" + publicKey +
                ",\"nonce\":" + salt +
                "\"timestamp\":" + (System.currentTimeMillis() / 1000L) +
                "}";
        return Tools.toBase64(output);
    }

    /**
     * Create a shared key for the key negotiation/agreement using ECDH(secp384r1)+HKDF(SHA512)
     *
     * @param publicKeyOther Public Key of the other person
     * @param privateKey     Your Private Key
     * @return Return the shared key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static Key createSharedKey(PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey, new ECGenParameterSpec("secp384r1"));
        Key keyAgreed = keyAgreement.doPhase(publicKeyOther, true);

        byte[] hkdfExtract = HKDFExtract(keyAgreed.getEncoded(), "Kono Dio da!");
        byte[] hkdfExpand = HKDFExpand(hkdfExtract, "ZA WARUDOOOOOO", 256);

        return new SecretKeySpec(hkdfExpand, "AES");
    }

    /**
     * Handle the message 1 received from other
     *
     * @param message1 Message 1 received from other
     * @return Return a SecureBuild
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public static SecretBuild handleMessage1(String message1) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        Message1 otherMessage = new Gson().fromJson(message1, Message1.class);
        int myNonce = 1; //Need to check if nonce is superior to the old message and increment every new message
        String myPubKey = ""; //Need to retrieve my pub key
        PrivateKey myPrivKey = ""; //Same as pub key
        PublicKey otherPubKey = Tools.getPublicKey(otherMessage.getPubKey());
        String symKey = Tools.toBase64(createSharedKey(otherPubKey, myPrivKey).getEncoded());
        return new SecretBuild((System.currentTimeMillis() / 1000L), otherMessage.getTimestamp(), myNonce, otherMessage.getNonce(), myPubKey, otherMessage.getPubKey(), symKey);
    }

    /**
     * Create message 2 (WIP)
     *
     * @param myDate
     * @param otherDate
     * @param myNonce
     * @param otherNonce
     * @param myPubKey
     * @param otherPubKey
     * @param symKey
     * @return
     */
    public static String createMessage2(long myDate, long otherDate, int myNonce, int otherNonce, String myPubKey, String otherPubKey, String symKey) {
        Gson gson = new GsonBuilder().create();
        gson.toJson(new SecretBuild(myDate, otherDate, myNonce, otherNonce, myPubKey, otherPubKey, symKey));
        return Tools.toBase64(gson.toString());
    }

    /**
     * Compare the shared key if is the same (WIP)
     *
     * @param sharedKey
     * @param publicKeyOther
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static boolean compareSharedKeys(Key sharedKey, PublicKey publicKeyOther, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        return sharedKey.equals(createSharedKey(publicKeyOther, privateKey));
    }
}
