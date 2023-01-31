package fr.upec.Prototype_E2EE.MyState;

import fr.upec.Prototype_E2EE.Protocol.Cipher;
import fr.upec.Prototype_E2EE.Protocol.Keys;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Store the PublicKey and the PrivateKey of the user
 * <pre>MUST BE HIDDEN!!! CONTAINS SENSITIVE INFORMATION!!!</pre>
 */
public class MyKeyPair {
    /**
     * Filename
     */
    public static final String FILENAME = ".MyKeyPair";
    private final PublicKey myPublicKey;
    private final PrivateKey myPrivateKey;

    /**
     * Constructor of MyKeyPair if file does not exist
     *
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException if there is an invalid or inappropriate algorithm parameter
     * @throws NoSuchAlgorithmException           Throws NoSuchAlgorithmException if there is not the expected algorithm
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
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private MyKeyPair(byte[] myPublicKeyBytes, byte[] myPrivateKeyBytes) throws GeneralSecurityException {
        this.myPublicKey = Tools.toPublicKey(myPublicKeyBytes);
        this.myPrivateKey = Tools.toPrivateKey(myPrivateKeyBytes);
    }

    /**
     * Load .MyKeyPair or generate a new one and return a MyKeyPair
     *
     * @return Return a MyKeyPair
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     * @throws IOException              Throws IOException if there is an I/O exception
     */
    public static MyKeyPair load(SecretKey secretKey) throws GeneralSecurityException, IOException {
        if (Tools.isFileExists(FILENAME)) {
            String output = new String(Cipher.decipher(secretKey, Tools.readFile(FILENAME)));
            String[] dataBase64 = output.split(",");
            return new MyKeyPair(Tools.toBytes(dataBase64[0]), Tools.toBytes(dataBase64[1]));
        } else {
            return new MyKeyPair();
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
     *
     * @throws IOException Throws IOException if there is an I/O exception
     */
    public void save(SecretKey secretKey) throws IOException, GeneralSecurityException {
        String myPublicKeyBase64 = Tools.toBase64(myPublicKey.getEncoded());
        String myPrivateKeyBase64 = Tools.toBase64(myPrivateKey.getEncoded());
        String input = myPublicKeyBase64 + "," + myPrivateKeyBase64;

        byte[] cipheredOutput = Cipher.cipher(secretKey, input.getBytes(StandardCharsets.UTF_8));

        Tools.writeToFile(FILENAME, cipheredOutput);
    }

    /**
     * Export PrivateKey to JSON for JS version
     * <pre>Contains nonce (salt), wkey, iterations, iv
     * See <a href="https://upec.ovh/e2ee.html">JS version</a></pre>
     *
     * @param hashedPassword Hashed password
     */
    public String exportToJSON(String hashedPassword) throws GeneralSecurityException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(myPrivateKey.getEncoded());
        KeyFactory keyFactoryPrivKey = KeyFactory.getInstance("EC");
        ECPrivateKey privateKey = (ECPrivateKey) keyFactoryPrivKey.generatePrivate(pkcs8EncodedKeySpec);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(myPublicKey.getEncoded());
        KeyFactory keyFactoryPubKey = KeyFactory.getInstance("EC");
        ECPublicKey publicKey = (ECPublicKey) keyFactoryPubKey.generatePublic(x509EncodedKeySpec);
        ECPoint ecPoint = publicKey.getW();

        String jwk = "{" +
                "\"crv\":\"P-256\"," +
                "\"d\":\"" + Tools.toBase64(privateKey.getS().toByteArray()) + "\"," +
                "\"ext\":true," +
                "\"key_ops\":[\"sign\"]," +
                "\"kty\":\"EC\"," +
                "\"x\":\"" + Tools.toBase64(ecPoint.getAffineX().toByteArray()) + "\"," +
                "\"y\":\"" + Tools.toBase64(ecPoint.getAffineY().toByteArray()) + "\"" +
                "}";

        byte[] nonce = Tools.generateRandomBytes(32);
        SecretKey secretKey = Tools.getSecretKeyPBKDF2(hashedPassword.toCharArray(), nonce);
        byte[] ciphered = Cipher.cipher(secretKey, jwk.getBytes(StandardCharsets.UTF_8));
        byte[] iv = Arrays.copyOfRange(ciphered, 0, 12);
        byte[] wkey = Arrays.copyOfRange(ciphered, 12, ciphered.length);
        return "{\"nonce\":\"" + Tools.toBase64(nonce) + "\"," +
                "\"wkey\":\"" + Tools.toBase64(wkey) + "\"," +
                "\"iterations\":" + Tools.PBKDF2_ITERATION + "," +
                "\"iv\":\"" + Tools.toBase64(iv) + "\"}";
    }
}
