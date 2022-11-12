package fr.upec.Prototype_E2EE;

public class SecretBuild {
    private long myDate;
    private long otherDate;
    private int myNonce;
    private int otherNonce;
    private String myPubKey;
    private String otherPubKey;
    private String symKey;

    public SecretBuild() {
    }

    public SecretBuild(long myDate, long otherDate, int myNonce, int otherNonce, String myPubKey, String otherPubKey, String symKey) {
        this.myDate = myDate;
        this.otherDate = otherDate;
        this.myNonce = myNonce;
        this.otherNonce = otherNonce;
        this.myPubKey = myPubKey;
        this.otherPubKey = otherPubKey;
        this.symKey = symKey;
    }

    public SecretBuild(SecretBuild mySecretBuild) {
        this.myDate = mySecretBuild.otherDate;
        this.otherDate = mySecretBuild.myDate;
        this.myNonce = mySecretBuild.otherNonce;
        this.otherNonce = mySecretBuild.myNonce;
        this.myPubKey = mySecretBuild.otherPubKey;
        this.otherPubKey = mySecretBuild.myPubKey;
        this.symKey = mySecretBuild.symKey;
    }

    public Boolean equals(SecretBuild other) {
        return this.myDate == other.otherDate &&
                this.otherDate == other.myDate &&
                this.myNonce == other.otherNonce &&
                this.otherNonce == other.myNonce &&
                this.myPubKey.equals(other.otherPubKey) &&
                this.otherPubKey.equals(other.myPubKey) &&
                this.symKey.equals(other.symKey);
    }


    public String getOtherPubKey() {
        return otherPubKey;
    }
}
