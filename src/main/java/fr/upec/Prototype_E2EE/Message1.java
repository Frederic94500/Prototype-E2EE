package fr.upec.Prototype_E2EE;

/**
 * Object for Message 1
 */
public class Message1 {
    private String pubKey;
    private int nonce;
    private long timestamp;

    /**
     * Default constructor for Gson
     */
    public Message1() {
    }

    /**
     * Message1 Constructor
     *
     * @param pubKey    PublicKey as Base64
     * @param nonce     Nonce (salt)
     * @param timestamp UNIX Timestamp
     */
    public Message1(String pubKey, int nonce, long timestamp) {
        this.pubKey = pubKey;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    /**
     * Getter for Public Key
     *
     * @return Return Public Key as Base64
     */
    public String getPubKey() {
        return pubKey;
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
     * Getter for Timestamp
     *
     * @return Return UNIX Timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}
