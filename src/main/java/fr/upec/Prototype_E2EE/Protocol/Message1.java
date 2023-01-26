package fr.upec.Prototype_E2EE.Protocol;

import fr.upec.Prototype_E2EE.Tools;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * Object for Message 1
 * <pre>timestamp = long = 8 bytes
 * nonce = byte[64] = 64 bytes
 * DHKeyPair -> PublicKey = 120 bytes
 * Message 1 total size = 192 bytes</pre>
 */
public class Message1 {
    private final long timestamp;
    private final byte[] nonce;
    private final KeyPair ECKeyPair;

    /**
     * Message1 Constructor
     *
     * @param timestamp UNIX Timestamp
     * @param nonce     Nonce (salt)
     */
    public Message1(long timestamp, int nonce) throws GeneralSecurityException {
        this.timestamp = timestamp;

        SecureRandom random = Tools.generateSecureRandom();
        random.setSeed(nonce);
        this.nonce = random.generateSeed(64);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), Tools.generateSecureRandom());
        this.ECKeyPair = keyPairGenerator.generateKeyPair();
    }

    /**
     * Encode Message1 to byte[]
     *
     * @return Return Message1 as byte[]
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(192);
        buffer.putLong(timestamp);
        buffer.put(nonce);
        buffer.put(ECKeyPair.getPublic().getEncoded());
        return buffer.array();
    }

    /**
     * Getter for Timestamp
     *
     * @return Return UNIX Timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for Nonce (salt)
     *
     * @return Return Nonce
     */
    public byte[] getNonce() {
        return nonce;
    }

    /**
     * Get Private key
     *
     * @return Return the Private key
     */
    public PrivateKey getPrivateKey() {
        return ECKeyPair.getPrivate();
    }

    /**
     * get Public key
     *
     * @return Return the Public key
     */
    public PublicKey getPublicKey() {
        return ECKeyPair.getPublic();
    }
}
