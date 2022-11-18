package fr.upec.Prototype_E2EE;

import java.nio.ByteBuffer;

/**
 * Object for Message 1
 * long = 8 bytes
 * int = 4 bytes
 * pubKey = 91 bytes
 */
public class Message1 {
    private final long timestamp;
    private final int nonce;
    private final byte[] pubKey;

    /**
     * Message1 Constructor
     *
     * @param timestamp UNIX Timestamp
     * @param nonce     Nonce (salt)
     * @param pubKey    PublicKey as Base64
     */
    public Message1(long timestamp, int nonce, byte[] pubKey) {
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.pubKey = pubKey;
    }

    /**
     * Encode Message1 to byte[]
     *
     * @return Return Message1 as byte[]
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(103);
        buffer.putLong(timestamp);
        buffer.putInt(nonce);
        buffer.put(pubKey);
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
    public int getNonce() {
        return nonce;
    }

    /**
     * Getter for Public Key
     *
     * @return Return Public Key as Base64
     */
    public byte[] getPubKey() {
        return pubKey;
    }
}
