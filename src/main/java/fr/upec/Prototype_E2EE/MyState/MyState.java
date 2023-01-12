package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.File;
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
    private final MyDirectory myDirectory;
    private final List<MyConversation> myConversations;
    private MyKeyPair myKeyPair;
    private int myNonce;

    /**
     * Create MyState
     */
    public MyState() throws GeneralSecurityException, IOException {
        this.myKeyPair = new MyKeyPair();
        this.myDirectory = new MyDirectory();
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
    public MyState(MyKeyPair myKeyPair, MyDirectory myDirectory, int myNonce, ArrayList<MyConversation> myConversations) {
        this.myKeyPair = myKeyPair;
        this.myDirectory = myDirectory;
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
                return new MyState(MyKeyPair.load(), new MyDirectory(), ByteBuffer.wrap(Tools.toBytes(dataBase64[2])).getInt(), getMyConversationsFromBase64(dataBase64));
            } else {
                throw new IllegalStateException("Not corresponding Key Pair");
            }
        } else {
            MyState myState = new MyState();
            myState.save();
            return myState;
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
        for (int i = 3; i < dataBase64.length; i++) {
            byte[] aConversation = Tools.toBytes(dataBase64[i]);
            myConversations.add(new MyConversation(new SecretBuild(aConversation)));
        }
        return myConversations;
    }

    /**
     * Verify if the digest of .MyKeyPair and .MyDirectory is the same
     *
     * @param dataBase64 Data in Base64
     * @return Return a Boolean
     */
    private static boolean isEqualsDigest(String[] dataBase64) throws IOException, NoSuchAlgorithmException {
        return dataBase64[0].equals(Tools.digest(MyKeyPair.filename)) && dataBase64[1].equals(Tools.digest(MyDirectory.filename));
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
     * Get MyDirectory
     *
     * @return Return MyDirectory
     */
    public MyDirectory getMyDirectory() {
        return myDirectory;
    }

    /**
     * Increment myNonce
     */
    public void incrementMyNonce() throws NoSuchAlgorithmException {
        int temp;
        do {
            temp = Tools.generateSecureRandom().nextInt(100);
        } while (temp == 0);
        this.myNonce += temp;
    }

    /**
     * Save MyState in a file
     * Contain digest .MyKeyPair, Base64 myNonce, all conversations in Base64
     */
    public void save() throws IOException, GeneralSecurityException {
        myKeyPair.save();
        myDirectory.saveIntoFile();
        String checksumMyKeyPair = Tools.digest(MyKeyPair.filename);
        String checksumMyDirectory = Tools.digest(MyDirectory.filename);
        String myNonceBase64 = Tools.toBase64(ByteBuffer.allocate(4).putInt(myNonce).array());
        ArrayList<String> arrayList = new ArrayList<>();
        for (MyConversation myConversation : myConversations) {
            arrayList.add(Tools.toBase64(myConversation.toBytes()));
        }
        String allConversations = String.join(",", arrayList);

        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        FileWriter writer = new FileWriter(filename);
        writer.write(checksumMyKeyPair + "," + checksumMyDirectory + "," + myNonceBase64 + "," + allConversations);
        writer.close();
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

    /**
     * Replace MyKeyPair by a new one and save the new one
     */
    public void replaceMyKeyPair() throws GeneralSecurityException, IOException {
        this.myKeyPair = new MyKeyPair();
        this.myKeyPair.save();
        this.save();
    }
}
