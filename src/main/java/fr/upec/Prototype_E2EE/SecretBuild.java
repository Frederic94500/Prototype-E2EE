package fr.upec.Prototype_E2EE;

public class SecretBuild {
    private long myDate;
    private long otherDate;
    private int myNonce;
    private int otherNonce;
    private String myPubKey;
    private String otherPubKey;
    private String symKey;

    public SecretBuild(long myDate, long otherDate, int myNonce, int otherNonce, String myPubKey, String otherPubKey, String symKey) {
        this.myDate = myDate;
        this.otherDate = otherDate;
        this.myNonce = myNonce;
        this.otherNonce = otherNonce;
        this.myPubKey = myPubKey;
        this.otherPubKey = otherPubKey;
        this.symKey = symKey;
    }
}
