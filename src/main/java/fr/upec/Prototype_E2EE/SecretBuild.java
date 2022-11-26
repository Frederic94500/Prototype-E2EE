package fr.upec.Prototype_E2EE;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Object for SecretBuild 238 bytes
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 * long myDate = 8 bytes
 * long otherDate = 8 bytes
 * int myNonce = 4 bytes
 * int otherNonce = 4 bytes
 * byte[] myPubKey = 91 bytes
 * byte[] otherPubKey = 91 bytes
 * byte[] symKey = 32 bytes
 */
public class SecretBuild {
    private final long myDate;
    private final long otherDate;
    private final int myNonce;
    private final int otherNonce;
    private final byte[] myPubKey;
    private final byte[] otherPubKey;
    private final byte[] symKey;

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
    public SecretBuild(long myDate, long otherDate, int myNonce, int otherNonce, byte[] myPubKey, byte[] otherPubKey, byte[] symKey) {
        this.myDate = myDate;
        this.otherDate = otherDate;
        this.myNonce = myNonce;
        this.otherNonce = otherNonce;
        this.myPubKey = myPubKey;
        this.otherPubKey = otherPubKey;
        this.symKey = symKey;
    }

    /**
     * Constructor to get the same SecretBuild of other
     *
     * @param mySecretBuild My SecretBuild
     */
    SecretBuild(SecretBuild mySecretBuild) {
        this.myDate = mySecretBuild.otherDate;
        this.otherDate = mySecretBuild.myDate;
        this.myNonce = mySecretBuild.otherNonce;
        this.otherNonce = mySecretBuild.myNonce;
        this.myPubKey = mySecretBuild.otherPubKey;
        this.otherPubKey = mySecretBuild.myPubKey;
        this.symKey = null;
    }

    /**
     * Constructor to load known information
     *
     * @param conversation Known information
     */
    public SecretBuild(byte[] conversation) {
        this.myDate = Tools.toLong(conversation, 0, 8);
        this.otherDate = Tools.toLong(conversation, 8, 16);
        this.myNonce = Tools.toInteger(conversation, 16, 20);
        this.otherNonce = Tools.toInteger(conversation, 20, 24);
        this.myPubKey = Arrays.copyOfRange(conversation, 24, 115);
        this.otherPubKey = Arrays.copyOfRange(conversation, 115, 206);
        this.symKey = Arrays.copyOfRange(conversation, 206, 238);
    }

    /**
     * Compare between SecretBuild
     *
     * @param other Other SecretBuild
     * @return Return a boolean if is the same
     */
    public Boolean equals(SecretBuild other) {
        return this.myDate == other.otherDate &&
                this.otherDate == other.myDate && //Nonce can't be compared
                Arrays.equals(this.myPubKey, other.otherPubKey) &&
                Arrays.equals(this.otherPubKey, other.myPubKey) &&
                Arrays.equals(this.symKey, other.symKey);
    }

    /**
     * Encode SecretBuild without symmetric key
     *
     * @return Return SecretBuild as byte[]
     */
    public byte[] toBytesWithoutSymKey() {
        ByteBuffer buffer = ByteBuffer.allocate(206);
        buffer.putLong(myDate);
        buffer.putLong(otherDate);
        buffer.putInt(myNonce);
        buffer.putInt(otherNonce);
        buffer.put(myPubKey);
        buffer.put(otherPubKey);
        return buffer.array();
    }

    /**
     * Encode SecretBuild with symmetric key
     *
     * @return Return SecretBuild as byte[]
     */
    public byte[] toBytesWithSymKey() {
        ByteBuffer buffer = ByteBuffer.allocate(238);
        buffer.putLong(myDate);
        buffer.putLong(otherDate);
        buffer.putInt(myNonce);
        buffer.putInt(otherNonce);
        buffer.put(myPubKey);
        buffer.put(otherPubKey);
        assert symKey != null;
        buffer.put(symKey);
        return buffer.array();
    }

    public long getMyDate() {
        return myDate;
    }

    public long getOtherDate() {
        return otherDate;
    }

    public int getMyNonce() {
        return myNonce;
    }

    public int getOtherNonce() {
        return otherNonce;
    }

    public byte[] getMyPubKey() {
        return myPubKey;
    }

    public byte[] getOtherPubKey() {
        return otherPubKey;
    }

    public byte[] getSymKey() {
        return symKey;
    }
}
