package fr.upec.Prototype_E2EE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Scanner;

/**
 * Store the PublicKey and the PrivateKey of the user
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyKeyPair {
    private static final String filename = ".MyKeyPair";
    private final PublicKey myPublicKey;
    private final PrivateKey myPrivateKey;

    /**
     * Constructor of MyKeyPair if file does not exist
     */
    public MyKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPair keyPair = Keys.generate();
        this.myPublicKey = keyPair.getPublic();
        this.myPrivateKey = keyPair.getPrivate();
    }

    /**
     * Constructor of MyKeyPair if file exist
     *
     * @param myPublicKeyBytes  Public Key in byte[]
     * @param myPrivateKeyBytes Private Key in byte[]
     */
    private MyKeyPair(byte[] myPublicKeyBytes, byte[] myPrivateKeyBytes) throws GeneralSecurityException {
        this.myPublicKey = Tools.toPublicKey(myPublicKeyBytes);
        this.myPrivateKey = Tools.toPrivateKey(myPrivateKeyBytes);
    }

    /**
     * Load .MyKeyPair or generate a new one and return a MyKeyPair
     *
     * @return Return a MyKeyPair
     */
    public static MyKeyPair load() throws GeneralSecurityException, FileNotFoundException {
        if (isFileExists()) {
            Scanner scanner = new Scanner(new File(filename));
            String data = scanner.nextLine();
            scanner.close();
            String[] dataBase64 = data.split(",");
            return new MyKeyPair(Tools.toBytes(dataBase64[0]), Tools.toBytes(dataBase64[1]));
        } else {
            createFile();
            MyKeyPair mkp = new MyKeyPair();
            mkp.save();
            return mkp;
        }
    }

    /**
     * Check if .MyKeyPair exists
     *
     * @return Return a boolean if the file exists
     */
    public static boolean isFileExists() {
        return new File(filename).exists();
    }

    /**
     * Create .MyKeyPair
     */
    public static void createFile() {
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile() {
        new File(filename).deleteOnExit();
    }

    /**
     * Get myPublicKey
     *
     * @return Return PublicKey
     */
    public PublicKey getMyPublicKey() {
        return myPublicKey;
    }

    /**
     * Get myPrivateKey
     *
     * @return Return PrivateKey
     */
    public PrivateKey getMyPrivateKey() {
        return myPrivateKey;
    }

    /**
     * Save MyKeyPair
     */
    private void save() {
        String myPublicKeyBase64 = Tools.toBase64(myPublicKey.getEncoded());
        String myPrivateKeyBase64 = Tools.toBase64(myPrivateKey.getEncoded());
        if (isFileExists()) {
            try {
                FileWriter writer = new FileWriter(filename);
                writer.write(myPublicKeyBase64 + "," + myPrivateKeyBase64);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException();
        }
    }
}
