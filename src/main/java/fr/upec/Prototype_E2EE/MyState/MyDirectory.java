package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.Cipher;
import fr.upec.Prototype_E2EE.Protocol.Sign;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        if (Tools.isFileExists(FILENAME)) {
            byte[] cipheredData = Tools.readFile(FILENAME);
            if (cipheredData.length != 0) {
                byte[] rawData = Cipher.decipher(secretKey, cipheredData);
                String[] users = new String(rawData).split(",");

                for (String user : users) {
                    String[] userInfo = user.split(":");
                    map.put(new String(Tools.toBytes(userInfo[0])), Tools.toBytes(userInfo[1]));
                }
            }
        }
        return map;
    }

    /**
     * Save MyDirectory to a file
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public void saveFile(SecretKey secretKey) throws IOException, GeneralSecurityException {
        String output = directory.entrySet().stream()
                .map(user -> Tools.toBase64(user.getKey().getBytes(StandardCharsets.UTF_8)) + ":" + Tools.toBase64(user.getValue()))
                .collect(Collectors.joining(","));

        if (directory.size() > 0) {
            byte[] cipheredDirectory = Cipher.cipher(secretKey, output.getBytes(StandardCharsets.UTF_8));
            Tools.writeToFile(FILENAME, cipheredDirectory);
        } else {
            Tools.deleteFile(FILENAME);
            Tools.createFile(FILENAME);
        }
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
    public String getSigner(byte[] signedMessage, byte[] expectedMessage2) throws GeneralSecurityException {
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            PublicKey otherPublicKey = Tools.toPublicKey(entry.getValue());
            try {
                if (Sign.verify(otherPublicKey, signedMessage, expectedMessage2)) {
                    return entry.getKey();
                }
            } catch (AEADBadTagException | SignatureException ignored) {
            }
        }
        throw new NoSuchElementException("Unknown sender!");
    }
}
