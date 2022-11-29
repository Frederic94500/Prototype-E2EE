package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.Keys;
import fr.upec.Prototype_E2EE.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Scanner;

/**
 * Store the PublicKey and the PrivateKey of the user
 * MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!
 */
public class MyKeyPair {
    public static final String filename = ".MyKeyPair";
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
    public static MyKeyPair load() throws GeneralSecurityException, IOException {
        if (Tools.isFileExists(filename)) {
            Scanner scanner = new Scanner(new File(filename));
            String data = scanner.nextLine();
            scanner.close();
            String[] dataBase64 = data.split(",");
            return new MyKeyPair(Tools.toBytes(dataBase64[0]), Tools.toBytes(dataBase64[1]));
        } else {
            Tools.createFile(filename);
            MyKeyPair mkp = new MyKeyPair();
            mkp.save();
            return mkp;
        }
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
    void save() throws IOException {
        String myPublicKeyBase64 = Tools.toBase64(myPublicKey.getEncoded());
        String myPrivateKeyBase64 = Tools.toBase64(myPrivateKey.getEncoded());
        if (!Tools.isFileExists(filename)) {
            Tools.createFile(filename);
        }
        FileWriter writer = new FileWriter(filename);
        writer.write(myPublicKeyBase64 + "," + myPrivateKeyBase64);
        writer.close();
    }
}
