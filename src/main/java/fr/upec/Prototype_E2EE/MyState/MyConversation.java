package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.SecretBuild;

/**
 * A conversation with the SecretBuild
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyConversation {
    private final SecretBuild mySecretBuild;

    /**
     * Constructor of MyConversation => Create a conversation
     *
     * @param mySecretBuild MySecretBuild
     */
    public MyConversation(SecretBuild mySecretBuild) {
        this.mySecretBuild = mySecretBuild;
    }

    /**
     * Get Symmetric Key from SecretBuild
     *
     * @return Return a Symmetric Key
     */
    public byte[] getSymKey() {
        return this.mySecretBuild.getSymKey();
    }

    /**
     * Get My Date from SecretBuild
     *
     * @return Return My Date
     */
    public long getMyDate() {
        return this.mySecretBuild.getMyDate();
    }

    /**
     * Get My Nonce from SecretBuild
     *
     * @return Return My Nonce
     */
    public int getMyNonce() {
        return mySecretBuild.getMyNonce();
    }

    /**
     * Get the Symmetric Key of the Conversation
     *
     * @return Return Symmetric Key
     */
    public byte[] toBytes() {
        return mySecretBuild.toBytesWithSymKey();
    }
}
