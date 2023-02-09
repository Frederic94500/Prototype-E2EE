package fr.upec.Prototype_E2EE.Protocol;

public class Conversation {
    private final String name;
    private final long date;
    private final byte[] secretKey;

    public Conversation(String name, long date, byte[] secretKey) {
        this.name = name;
        this.date = date;
        this.secretKey = secretKey;
    }

    public String getName() {
        return name;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public long getDate() {
        return date;
    }
}
