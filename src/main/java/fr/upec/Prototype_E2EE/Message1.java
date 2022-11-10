package fr.upec.Prototype_E2EE;

public class Message1 {
    private String pubKey;
    private int nonce;
    private long timestamp;

    public Message1() {
    }

    public Message1(String pubKey, int nonce, long timestamp) {
        this.pubKey = pubKey;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }


    public String getPubKey() {
        return pubKey;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
