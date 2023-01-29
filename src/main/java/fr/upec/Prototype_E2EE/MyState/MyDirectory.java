package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.Cipher;
import fr.upec.Prototype_E2EE.Protocol.Sign;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * MyDirectory contains a list of persons
 */
public class MyDirectory {
    /**
     * Filename
     */
    public final static String FILENAME = ".MyDirectory";
    private final HashMap<String, byte[]> directory;

    public MyDirectory() {
        this.directory = new HashMap<>();
    }

    /**
     * Constructor MyDirectory
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public MyDirectory(SecretKey secretKey) throws IOException, GeneralSecurityException {
        this.directory = readFile(secretKey);
    }

    /**
     * Read .MyDirectory
     *
     * @return Return HashMap
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public HashMap<String, byte[]> readFile(SecretKey secretKey) throws IOException, GeneralSecurityException {
        HashMap<String, byte[]> map = new HashMap<>();
        if (!Tools.isFileExists(FILENAME)) {
            Tools.createFile(FILENAME);
        }
        File file = new File(FILENAME);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String output = new String(Cipher.decipher(secretKey, Tools.toBytes(line)));

                String[] tab = output.split(":");
                String decodedName = new String(Tools.toBytes(tab[0]));
                byte[] decodedPubKey = Tools.toBytes(tab[1]);

                map.put(decodedName, decodedPubKey);
            }
        }
        return map;
    }

    /**
     * Save MyDirectory to a file
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public void saveIntoFile(SecretKey secretKey) throws IOException, GeneralSecurityException {
        if (!Tools.isFileExists(FILENAME)) {
            Tools.createFile(FILENAME);
        }
        writeToFile(secretKey);
    }

    /**
     * Write MyDirectory to a file
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    private void writeToFile(SecretKey secretKey) throws IOException, GeneralSecurityException {
        FileWriter fw = new FileWriter(FILENAME);
        BufferedWriter bw = new BufferedWriter(fw);
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            String encodedNameString = Tools.toBase64(entry.getKey().getBytes(StandardCharsets.UTF_8));
            String encodedPubKey = Tools.toBase64(entry.getValue());
            String entryLine = encodedNameString + ":" + encodedPubKey;

            byte[] cipheredEntry = Cipher.cipher(secretKey, entryLine.getBytes(StandardCharsets.UTF_8));

            bw.write(Tools.toBase64(cipheredEntry));
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

    /**
     * Get Key name in directory
     *
     * @param otherPubKey Other Public Key
     * @return Return the Key name or null if it does not found
     */
    public String getKeyName(byte[] otherPubKey) {
        if (isInDirectory(otherPubKey)) {
            for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
                if (Arrays.equals(otherPubKey, entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Get the user who signed the message
     *
     * @param signedMessage    Signed message
     * @param expectedMessage2 Expected Message 2
     * @return Return the name of the signer or null if not found
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public String getSigner(byte[] signedMessage, String expectedMessage2) throws GeneralSecurityException {
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            PublicKey otherPublicKey = Tools.toPublicKey(entry.getValue());
            if (Sign.verify(otherPublicKey, signedMessage, expectedMessage2)) {
                return entry.getKey();
            }
        }
        throw new NoSuchElementException("Unknown sender!");
    }
}
