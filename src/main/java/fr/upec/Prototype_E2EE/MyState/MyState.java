package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contain user state
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyState {
    public static final String filename = ".MyState";
    private final MyKeyPair myKeyPair;
    private final List<MyConversation> myConversations;
    private int myNonce;

    /**
     * Create MyState
     */
    public MyState() throws GeneralSecurityException, FileNotFoundException {
        this.myKeyPair = MyKeyPair.load();
        this.myNonce = 0;
        this.myConversations = new ArrayList<>();
    }

    /**
     * Create MyState from known information
     *
     * @param myKeyPair       MyKeyPair
     * @param myNonce         MyNonce
     * @param myConversations MyConversations
     */
    public MyState(MyKeyPair myKeyPair, int myNonce, ArrayList<MyConversation> myConversations) {
        this.myKeyPair = myKeyPair;
        this.myNonce = myNonce;
        this.myConversations = myConversations;
    }

    /**
     * Load or create a MyState
     *
     * @return Return MyState
     */
    public static MyState load() throws IOException, GeneralSecurityException {
        if (Tools.isFileExists(filename)) {
            Scanner scanner = new Scanner(new File(filename));
            String data = scanner.nextLine();
            scanner.close();
            String[] dataBase64 = data.split(",");
            if (isEqualsDigest(dataBase64)) {
                return new MyState(MyKeyPair.load(), ByteBuffer.wrap(Tools.toBytes(dataBase64[1])).getInt(), getMyConversationsFromBase64(dataBase64));
            } else {
                throw new IllegalStateException("Not corresponding Key Pair");
            }
        } else {
            Tools.createFile(filename);
            return new MyState();
        }
    }

    /**
     * Convert all Conversations Base64 to MyConversation object
     *
     * @param dataBase64 MyState in Base64
     * @return Return all Conversations
     */
    private static ArrayList<MyConversation> getMyConversationsFromBase64(String[] dataBase64) {
        ArrayList<MyConversation> myConversations = new ArrayList<>();
        for (int i = 2; i < dataBase64.length; i++) {
            byte[] aConversation = Tools.toBytes(dataBase64[i]);
            myConversations.add(new MyConversation(new SecretBuild(aConversation)));
        }
        return myConversations;
    }

    /**
     * Verify is the digest of .MyKeyPair is the same
     *
     * @param dataBase64 Digest of the file
     * @return Return a Boolean
     */
    private static boolean isEqualsDigest(String[] dataBase64) throws IOException, NoSuchAlgorithmException {
        return dataBase64[0].equals(MyKeyPair.digest());
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

    /**
     * Save MyState in a file
     * Contain digest .MyKeyPair, Base64 myNonce, all conversations in Base64
     */
    public void save() throws IOException, NoSuchAlgorithmException {
        String checksumMyKeyPair = MyKeyPair.digest();
        String myNonceBase64 = Tools.toBase64(ByteBuffer.allocate(4).putInt(myNonce).array());
        ArrayList<String> arrayList = new ArrayList<>();
        for (MyConversation myConversation : myConversations) {
            arrayList.add(Tools.toBase64(myConversation.toBytes()));
        }
        String allConversations = String.join(",", arrayList);

        if (Tools.isFileExists(filename)) {
            FileWriter writer = new FileWriter(filename);
            writer.write(checksumMyKeyPair + "," + myNonceBase64 + "," + allConversations);
            writer.close();
        } else {
            Tools.createFile(filename);
            FileWriter writer = new FileWriter(filename);
            writer.write(checksumMyKeyPair + "," + myNonceBase64 + "," + allConversations);
            writer.close();
        }
    }

    /**
     * Add a new conversation to the list of conversations
     *
     * @param secretBuild SecretBuild from the new conversation
     */
    public void addAConversation(SecretBuild secretBuild) {
        myConversations.add(new MyConversation(secretBuild));
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
