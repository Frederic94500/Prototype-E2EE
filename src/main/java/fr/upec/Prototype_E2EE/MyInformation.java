package fr.upec.Prototype_E2EE;

import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contain user information
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyInformation {
    private final String filename = ".MyState";
    private final MyKeyPair myKeyPair;
    private final List<MyConversation> myConversations;
    private int myNonce;

    /**
     * Create MyInformation
     */
    public MyInformation() throws GeneralSecurityException, FileNotFoundException {
        this.myKeyPair = MyKeyPair.load();
        this.myNonce = 0;
        this.myConversations = new ArrayList<>();
    }

    /**
     * Get MyKeyPair
     *
     * @return Return MyKeyPair
     */
    public MyKeyPair getMyKeyPair() {
        return myKeyPair;
    }

    /**
     * Get my nonce
     *
     * @return int MyNonce
     */
    public int getMyNonce() {
        return myNonce;
    }

    /**
     * Get all conversations
     *
     * @return Return ArrayList MyConversations
     */
    public List<MyConversation> getMyConversations() {
        return myConversations;
    }

    /**
     * Increment myNonce
     */
    public void incrementMyNonce() throws NoSuchAlgorithmException {
        this.myNonce += Tools.generateSecureRandom().nextInt(100);
    }

    public void save() {

    }

    /**
     * Delete a conversation
     *
     * @param nonce myNonce
     */
    public void deleteAConversation(int nonce) {
        for (int i = 0; i < myConversations.size(); i++) {
            if (myConversations.get(i).getMyNonce() == nonce) {
                myConversations.remove(i);
                break;
            }
        }
    }
}
