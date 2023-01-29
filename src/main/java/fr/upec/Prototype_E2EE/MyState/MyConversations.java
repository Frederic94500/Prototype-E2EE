package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.Cipher;
import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * MyConversations contains SecretBuild for each conversation
 * <pre>MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!</pre>
 */
public class MyConversations {
    /**
     * Filename
     */
    public static final String FILENAME = ".MyConversations";
    private final List<SecretBuild> myConversations;

    public MyConversations() {
        this.myConversations = new ArrayList<>();
    }

    /**
     * Constructor for MyConversations
     *
     * @throws FileNotFoundException Throws FileNotFoundException if the file was not found
     */
    public MyConversations(SecretKey secretKey) throws FileNotFoundException, GeneralSecurityException {
        this.myConversations = load(secretKey);
    }

    /**
     * Load .MyConversations and create an ArrayList
     *
     * @return Return an ArrayList of SecretBuild
     * @throws FileNotFoundException Throws FileNotFoundException if the file was not found
     */
    public List<SecretBuild> load(SecretKey secretKey) throws FileNotFoundException, GeneralSecurityException {
        ArrayList<SecretBuild> myConversations = new ArrayList<>();
        if (Tools.isFileExists(FILENAME)) {
            Scanner scanner = new Scanner(new File(FILENAME));
            if (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                scanner.close();
                String output = new String(Cipher.decipher(secretKey, Tools.toBytes(data)));
                String[] rawConversations = output.split(",");
                for (String rawConversation : rawConversations) {
                    String[] splitConversation = rawConversation.split(":");
                    myConversations.add(new SecretBuild(new String(Tools.toBytes(splitConversation[0])), Tools.toBytes(splitConversation[1])));
                }
            }
        }
        return myConversations;
    }

    /**
     * Save MyConversations to .MyConversations
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public void save(SecretKey secretKey) throws IOException, GeneralSecurityException {
        String rawConversations = myConversations.stream()
                .map(secretBuild -> Tools.toBase64(secretBuild.getName().getBytes(StandardCharsets.UTF_8)) + ":" + Tools.toBase64(secretBuild.toBytesWithSymKey()))
                .collect(Collectors.joining(","));

        if (myConversations.size() > 0) {
            byte[] cipheredOutput = Cipher.cipher(secretKey, rawConversations.getBytes(StandardCharsets.UTF_8));
            Tools.writeToFile(FILENAME, cipheredOutput);
        } else {
            Tools.createFile(FILENAME);
        }
    }

    /**
     * Get size of the list of conversations
     *
     * @return Return the size of the list of conversations
     */
    public int getSize() {
        return myConversations.size();
    }

    /**
     * Add a new conversation to the list of conversations
     *
     * @param secretBuild SecretBuild to be added
     */
    public void addConversation(SecretBuild secretBuild) {
        myConversations.add(secretBuild);
    }

    /**
     * Get a conversation
     *
     * @param index Index of the conversation
     * @return Return a conversation (as SecretBuild)
     */
    public SecretBuild getConversation(int index) {
        return myConversations.get(index);
    }

    /**
     * Delete a conversation
     *
     * @param secretBuild Conversation (as SecretBuild) to be deleted
     */
    public void deleteConversation(SecretBuild secretBuild) {
        myConversations.remove(secretBuild);
    }
}
