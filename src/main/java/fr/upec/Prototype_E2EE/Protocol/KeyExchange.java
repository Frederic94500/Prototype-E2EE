package fr.upec.Prototype_E2EE.Protocol;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;

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
    public static byte[] hkdfExtract(byte[] salt, SecretKey ikm) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(ikm);
        mac.update(salt);
        return mac.doFinal();
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
    public static byte[] hkdfExpand(byte[] prk, String info, int olb) throws NoSuchAlgorithmException, InvalidKeyException {
        if (olb <= 0 || prk == null) {
            throw new IllegalArgumentException();
        }

        SecretKey secretKey = new SecretKeySpec(prk, "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKey);

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
     * Create a shared key for the key negotiation/agreement using ECDH+HKDF(SHA512)
     *
     * @param privateKey     Your Private Key
     * @param publicKeyOther Public Key of the other person
     * @return Return the shared key
     */
    public static Key createSharedKey(PrivateKey privateKey, PublicKey publicKeyOther, int myNonce, int otherNonce, String info) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKeyOther, true);
        byte[] keyAgreed = keyAgreement.generateSecret();
        SecretKey symKey = new SecretKeySpec(keyAgreed, "ECDH");

        int xor = myNonce ^ otherNonce;

        byte[] hkdfExtract = hkdfExtract(ByteBuffer.allocate(4).putInt(xor).array(), symKey);
        byte[] hkdfExpand = hkdfExpand(hkdfExtract, info, 32);

        return new SecretKeySpec(hkdfExpand, "AES");
    }
}
