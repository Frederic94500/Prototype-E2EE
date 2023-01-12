package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Tools;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyDirectory {
    public final static String filename = ".MyDirectory";
    private final HashMap<String, byte[]> directory;

    /**
     * Constructor MyDirectory
     */
    public MyDirectory() throws IOException {
        this.directory = readFile();
    }

    /**
     * Read .MyDirectory
     *
     * @return Return HashMap
     */
    public HashMap<String, byte[]> readFile() throws IOException {
        HashMap<String, byte[]> map = new HashMap<>();
        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        File file = new File(filename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tab = line.split(":");
                String decodedName = new String(Tools.toBytes(tab[0]));
                byte[] decodedPubKey = Tools.toBytes(tab[1]);

                map.put(decodedName, decodedPubKey);
            }
        }
        return map;
    }

    /**
     * Save MyDirectory to a file
     */
    public void saveIntoFile() throws IOException {
        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        writeToFile();
    }

    /**
     * Write MyDirectory to a file
     */
    private void writeToFile() throws IOException {
        FileWriter fw = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fw);
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            String encodedNameString = Tools.toBase64(entry.getKey().getBytes());
            String encodedPubKey = Tools.toBase64(entry.getValue());

            bw.write(encodedNameString + ":" + encodedPubKey);
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    /**
     * Search in Directory if the otherPubKey is present
     *
     * @param otherPubKey The otherPubKey
     * @return Return a boolean if it is present
     */
    public boolean isInDirectory(byte[] otherPubKey) {
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            if (Arrays.equals(otherPubKey, entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search in Directory if the key is present
     *
     * @param key The key entry
     * @return Return a boolean if it is present
     */
    public boolean isInDirectory(String key) {
        return directory.containsKey(key);
    }

    /**
     * Add a person in MyDirectory
     *
     * @param name   Name of the person
     * @param pubKey Public Key of the person
     */
    public void addPerson(String name, byte[] pubKey) {
        directory.put(name, pubKey);
    }

    /**
     * Get size of MyDirectory
     *
     * @return Return the size of MyDirectory
     */
    public int sizeOfDirectory() {
        return directory.size();
    }

    /**
     * Delete a person in MyDirectory
     *
     * @param name Name of the person
     */
    public void deletePerson(String name) {
        directory.remove(name);
    }

    /**
     * Get the Public Key of a person
     *
     * @param name Name of the person
     * @return Return the Public Key of the person
     */
    public byte[] getPerson(String name) {
        return directory.get(name);
    }

    /**
     * Show Directory
     *
     * @return Return the Directory
     */
    public String showDirectory() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(Tools.toBase64(entry.getValue())).append("\n");
        }
        return sb.toString();
    }
}
