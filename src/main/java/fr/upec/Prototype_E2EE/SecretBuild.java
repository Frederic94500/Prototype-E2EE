package fr.upec.Prototype_E2EE;

/**
 * Object for SecretBuild
 * MUST BE HIDDEN! CONTAINS SENSITIVE INFORMATION!
 */
public class SecretBuild {
    private long myDate;
    private long otherDate;
    private int myNonce;
    private int otherNonce;
    private String myPubKey;
    private String otherPubKey;
    private String symKey;

    /**
     * Default constructor for Gson
     */
    public SecretBuild() {
    }

    /**
     * SecretBuild Constructor
     *
     * @param myDate      My Date as UNIX Timestamp
     * @param otherDate   Other Date as UNIX Timestamp
     * @param myNonce     My Nonce (salt)
     * @param otherNonce  Other Nonce (salt)
     * @param myPubKey    My Public Key as Base64
     * @param otherPubKey My Public Key as Base64
     * @param symKey      Symmetric Key as Base64
     */
    public SecretBuild(long myDate, long otherDate, int myNonce, int otherNonce, String myPubKey, String otherPubKey, String symKey) {
        this.myDate = myDate;
        this.otherDate = otherDate;
        this.myNonce = myNonce;
        this.otherNonce = otherNonce;
        this.myPubKey = myPubKey;
        this.otherPubKey = otherPubKey;
        this.symKey = symKey;
    }

    /**
     * Constructor for swapping information
     *
     * @param mySecretBuild My SecretBuild
     */
    public SecretBuild(SecretBuild mySecretBuild) {
        this.myDate = mySecretBuild.otherDate;
        this.otherDate = mySecretBuild.myDate;
        this.myNonce = mySecretBuild.otherNonce;
        this.otherNonce = mySecretBuild.myNonce;
        this.myPubKey = mySecretBuild.otherPubKey;
        this.otherPubKey = mySecretBuild.myPubKey;
        this.symKey = mySecretBuild.symKey;
    }

    /**
     * Compare between SecretBuild
     *
     * @param other Other SecretBuild
     * @return Return a boolean if is the same
     * @deprecated Maybe useless
     */
    public Boolean equals(SecretBuild other) {
        return this.myDate == other.otherDate &&
                this.otherDate == other.myDate &&
                this.myNonce == other.otherNonce &&
                this.otherNonce == other.myNonce &&
                this.myPubKey.equals(other.otherPubKey) &&
                this.otherPubKey.equals(other.myPubKey) &&
                this.symKey.equals(other.symKey);
    }

    /**
     * Getter for Other Public Key
     *
     * @return Return Other Public Key as Base64
     */
    public String getOtherPubKey() {
        return otherPubKey;
    }
}
