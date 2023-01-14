package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * MyConversations contains SecretBuild for each conversation
 * <pre>MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!</pre>
 */
public class MyConversations {
    public static final String filename = ".MyConversations";
    private final List<SecretBuild> myConversations;

    /**
     * Constructor for MyConversations
     *
     * @throws FileNotFoundException Throw FileNotFoundException if the file was not found
     */
    public MyConversations() throws FileNotFoundException {
        this.myConversations = load();
    }

    /**
     * Load .MyConversations and create an ArrayList
     *
     * @return Return an ArrayList of SecretBuild
     * @throws FileNotFoundException Throw FileNotFoundException if the file was not found
     */
    public List<SecretBuild> load() throws FileNotFoundException {
        ArrayList<SecretBuild> myConversations = new ArrayList<>();
        if (Tools.isFileExists(filename)) {
            Scanner scanner = new Scanner(new File(filename));
            if (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                scanner.close();
                String[] rawConversations = data.split(",");
                for (String rawConversation : rawConversations) {
                    myConversations.add(new SecretBuild(Tools.toBytes(rawConversation)));
                }
            }
        }
        return myConversations;
    }

    /**
     * Save MyConversations to .MyConversations
     *
     * @throws IOException Throw IOException if there is an I/O exception
     */
    public void save() throws IOException {
        String rawConversations = myConversations.stream()
                .map(secretBuild -> Tools.toBase64(secretBuild.toBytesWithSymKey()))
                .collect(Collectors.joining(","));

        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        FileWriter writer = new FileWriter(filename);
        writer.write(rawConversations);
        writer.close();
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
