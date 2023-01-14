package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

/**
 * Contain user state
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyState {
    public static final String filename = ".MyState";
    private final MyDirectory myDirectory;
    private final MyConversations myConversations;
    private MyKeyPair myKeyPair;
    private int myNonce;

    /**
     * Create MyState
     */
    public MyState() throws GeneralSecurityException, IOException {
        this.myKeyPair = new MyKeyPair();
        this.myDirectory = new MyDirectory();
        this.myConversations = new MyConversations();
        this.myNonce = 0;
    }

    /**
     * Create MyState from known information
     *
     * @param myKeyPair       MyKeyPair
     * @param myDirectory     MyDirectory
     * @param myConversations MyConversations
     * @param myNonce         MyNonce
     */
    public MyState(MyKeyPair myKeyPair, MyDirectory myDirectory, MyConversations myConversations, int myNonce) {
        this.myKeyPair = myKeyPair;
        this.myDirectory = myDirectory;
        this.myConversations = myConversations;
        this.myNonce = myNonce;
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
            String[] rawData = data.split(",");
            if (isEqualsDigest(rawData)) {
                return new MyState(MyKeyPair.load(),
                        new MyDirectory(),
                        new MyConversations(),
                        ByteBuffer.wrap(Tools.toBytes(rawData[3])).getInt());
            } else {
                throw new IllegalStateException("""
                        WARNING!!! YOUR FILES HAS BEEN COMPROMISED!
                        PLEASE ERASE .MyState, .MyKeyPair, .MyDirectory AND .MyConversations!!!""");
            }
        } else {
            MyState myState = new MyState();
            myState.save();
            return myState;
        }
    }

    /**
     * Verify if the digest of .MyKeyPair, .MyDirectory and .MyConversations is the same
     *
     * @param rawData Data from .MyState
     * @return Return a Boolean
     */
    private static boolean isEqualsDigest(String[] rawData) throws IOException, NoSuchAlgorithmException {
        return rawData[0].equals(Tools.digest(MyKeyPair.filename))
                && rawData[1].equals(Tools.digest(MyDirectory.filename))
                && rawData[2].equals(Tools.digest(MyConversations.filename));
    }

    /**
     * Get my Public Key from MyKeyPair
     *
     * @return Return my Public Key
     */
    public PublicKey getMyPublicKey() {
        return myKeyPair.getMyPublicKey();
    }

    /**
     * Get my Private Key from MyKeyPair
     *
     * @return Return my Private Key
     */
    public PrivateKey getMyPrivateKey() {
        return myKeyPair.getMyPrivateKey();
    }

    /**
     * Get all conversations
     *
     * @return Return ArrayList MyConversations
     */
    public MyConversations getMyConversations() {
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
     * Get my nonce
     *
     * @return int MyNonce
     */
    public int getMyNonce() {
        return myNonce;
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
        myConversations.save();
        String checksumMyKeyPair = Tools.digest(MyKeyPair.filename);
        String checksumMyDirectory = Tools.digest(MyDirectory.filename);
        String checksumMyConversations = Tools.digest(MyConversations.filename);
        String myNonceBase64 = Tools.toBase64(ByteBuffer.allocate(4).putInt(myNonce).array());

        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        FileWriter writer = new FileWriter(filename);
        writer.write(checksumMyKeyPair + "," + checksumMyDirectory + "," + checksumMyConversations + "," + myNonceBase64);
        writer.close();
    }

    /**
     * Add a new conversation to the list of conversations
     *
     * @param secretBuild SecretBuild from the new conversation
     */
    public void addAConversation(SecretBuild secretBuild) {
        myConversations.addConversation(secretBuild);
    }

    /**
     * Get the size of conversations
     *
     * @return Return the size of conversations
     */
    public int getConversationSize() {
        return myConversations.getSize();
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
