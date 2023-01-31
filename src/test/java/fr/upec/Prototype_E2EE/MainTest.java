package fr.upec.Prototype_E2EE;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.upec.Prototype_E2EE.MyState.MyConversations;
import fr.upec.Prototype_E2EE.MyState.MyDirectory;
import fr.upec.Prototype_E2EE.MyState.MyKeyPair;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    static private MyState user1;
    static private MyState user2;
    static private SecretBuild sbUser1;
    static private SecretBuild sbUser2;

    @BeforeClass
    public static void setupClass() throws GeneralSecurityException, IOException {
        user1 = new MyState(Tools.hashPassword("toto"));
        user2 = new MyState(Tools.hashPassword("tata"));
    }

    @Before
    public void deleteFilesBefore() {
        Tools.deleteFile(MyState.FILENAME);
        Tools.deleteFile(MyKeyPair.FILENAME);
        Tools.deleteFile(MyDirectory.FILENAME);
        Tools.deleteFile(MyConversations.FILENAME);
    }

    @After
    public void deleteFilesAfter() {
        Tools.deleteFile(MyState.FILENAME);
        Tools.deleteFile(MyKeyPair.FILENAME);
        Tools.deleteFile(MyDirectory.FILENAME);
        Tools.deleteFile(MyConversations.FILENAME);
    }

    @Test
    public void testMessage1() throws GeneralSecurityException {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, user1.getMyNonce());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce());

        user2.getMyDirectory().addPerson("user1", user1.getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(message1User2, message1User1For2);

        user1.getMyDirectory().addPerson("user2", user2.getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(message1User1, message1User2For1);

        assertEquals(message1User1.getTimestamp(), secretBuildUser2.getOtherDate());
        assertArrayEquals(message1User1.getNonce(), secretBuildUser2.getOtherNonce());
        assertArrayEquals(message1User1.getPublicKey().getEncoded(), secretBuildUser2.getOtherPubKey());

        assertEquals(message1User2.getTimestamp(), secretBuildUser1.getOtherDate());
        assertArrayEquals(message1User2.getNonce(), secretBuildUser1.getOtherNonce());
        assertArrayEquals(message1User2.getPublicKey().getEncoded(), secretBuildUser1.getOtherPubKey());
    }

    @Test
    public void testMessage2() throws Exception {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, user1.getMyNonce());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce());

        user2.getMyDirectory().addPerson("user1", user1.getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(message1User2, message1User1For2);

        user1.getMyDirectory().addPerson("user2", user2.getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(message1User1, message1User2For1);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));
        sbUser1 = secretBuildUser1;
        sbUser2 = secretBuildUser2;

        String message2User1 = Communication.createMessage2(user1.getMyPrivateKey(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getMyPrivateKey(), secretBuildUser2);

        assertEquals("user1", Communication.handleMessage2(user2.getMyDirectory(), secretBuildUser2, message2User1));
        assertEquals("user2", Communication.handleMessage2(user1.getMyDirectory(), secretBuildUser1, message2User2));
    }

    @Test
    public void testSigningVerifying() throws GeneralSecurityException {
        String textString = "Around the World, Around the World";
        byte[] signatureUser1 = Sign.sign(user1.getMyPrivateKey(), textString);
        String signatureBase64User1 = Tools.toBase64(signatureUser1);

        byte[] signatureFromUser1 = Tools.toBytes(signatureBase64User1);
        assertTrue(Sign.verify(user1.getMyPublicKey(), signatureFromUser1, textString));
    }

    @Test
    public void testCipherDecipher() throws GeneralSecurityException {
        String textString = "Moeagare Moeagare GANDAMU!";
        byte[] cipheredTextUser1 = Cipher.cipher(Tools.toSecretKey(sbUser1.getSymKey()), textString.getBytes(StandardCharsets.UTF_8));
        String cipheredTextBase64User1 = Tools.toBase64(cipheredTextUser1);

        byte[] cipheredTextFromUser1 = Tools.toBytes(cipheredTextBase64User1);
        assertEquals(textString, new String(Cipher.decipher(Tools.toSecretKey(sbUser2.getSymKey()), cipheredTextFromUser1)));
    }

    @Test
    public void testSaveAndLoadMyKeyPair() throws GeneralSecurityException, IOException {
        SecretKey secretKey = Tools.getSecretKeyPBKDF2("1234".toCharArray(), Tools.generateRandomBytes(32), 1024);
        MyKeyPair myKeyPair = new MyKeyPair(); //Without File
        myKeyPair.save(secretKey);
        MyKeyPair myKeyPairViaFile = MyKeyPair.load(secretKey); //With File

        assertArrayEquals(myKeyPair.getMyPublicKey().getEncoded(), myKeyPairViaFile.getMyPublicKey().getEncoded());
        assertArrayEquals(myKeyPair.getMyPrivateKey().getEncoded(), myKeyPairViaFile.getMyPrivateKey().getEncoded());
    }

    @Test
    public void testSaveAndLoadMyState() throws GeneralSecurityException, IOException {
        String hashedPassword = Tools.hashPassword("1234");
        MyState myState = new MyState(hashedPassword);
        myState.save();

        SecretKey newSecretKey = Tools.loadSecretKey(hashedPassword);
        MyState myStateFile = MyState.load(hashedPassword, newSecretKey);

        assertArrayEquals(myState.getMyPublicKey().getEncoded(), myStateFile.getMyPublicKey().getEncoded());
        assertArrayEquals(myState.getMyPrivateKey().getEncoded(), myStateFile.getMyPrivateKey().getEncoded());
        assertEquals(myState.getMyNonce(), myStateFile.getMyNonce());
    }

    @Test
    public void testMyDirectory() throws IOException, GeneralSecurityException {
        String hashedPassword = Tools.hashPassword("1234");
        SecretKey secretKey = Tools.getSecretKeyPBKDF2(hashedPassword.toCharArray(), Tools.generateRandomBytes(32), 1024);

        MyDirectory myDirectory = new MyDirectory();
        assertEquals(0, myDirectory.sizeOfDirectory());

        myDirectory.addPerson("user1", user1.getMyPublicKey().getEncoded());
        assertEquals(1, myDirectory.sizeOfDirectory());

        myDirectory.addPerson("user2", user2.getMyPublicKey().getEncoded());
        assertEquals(2, myDirectory.sizeOfDirectory());

        myDirectory.saveFile(secretKey);
        MyDirectory myDirectoryFile = new MyDirectory(secretKey);

        assertTrue(myDirectory.isInDirectory(user1.getMyPublicKey().getEncoded()));
        assertTrue(myDirectoryFile.isInDirectory(myDirectory.getPerson("user2")));

        myDirectory.deletePerson("user2");
        assertEquals(1, myDirectory.sizeOfDirectory());
        myDirectory.saveFile(secretKey);

        MyDirectory myDirectoryFile2 = new MyDirectory(secretKey);
        assertEquals(1, myDirectoryFile2.sizeOfDirectory());
    }

    @Test
    public void testReplaceMyKeyPair() throws GeneralSecurityException, IOException {
        String hashedPassword = Tools.hashPassword("1234");

        MyState myState = new MyState(hashedPassword);
        myState.save();

        SecretKey secretKey = Tools.loadSecretKey(hashedPassword);
        MyState myStateFile = MyState.load(hashedPassword, secretKey);
        String digestFile = Tools.digest(MyKeyPair.FILENAME);

        myState.replaceMyKeyPair();

        assertFalse(Arrays.equals(myStateFile.getMyPublicKey().getEncoded(), myState.getMyPublicKey().getEncoded()));
        assertNotEquals(digestFile, Tools.digest(MyKeyPair.FILENAME));
    }

    @Test
    public void testParserPubKey() {
        String pemKey = "-----BEGIN PUBLIC KEY-----MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==-----END PUBLIC KEY-----";
        String pubKey = Tools.keyParser(pemKey);
        String reTestPubKey = Tools.keyParser(pubKey);
        String wrongPubKey = "I'm sorry, Dave. I'm afraid I can't do that.";

        assertEquals("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==", pubKey);
        assertEquals("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==", reTestPubKey);
        assertThrows(IllegalArgumentException.class, () -> Tools.keyParser(wrongPubKey));
    }

    @Test
    public void testJWK() throws GeneralSecurityException {
        String jwk = "{\"nonce\":\"yzcHGSJ388h5/6Mct0OR3swFGT6R9NrI3xDs6arV9UE=\",\"wkey\":\"9DG/Lo4kH6ckJNUoOlpJe2YnPEVOmujnAPMCuF22zBVs+CheQwuAU3bTaOedkCuw1x4CtSnX2Tes21pnav4KR0/KYDs8u/6B3DlnKoyOo0w/98vIGvifLHi6fB8tm1e40aXBmjk51LYBdCEecfy/hkOHJFSwYUqFT4aeH542GGnVxHB2fu7S/HTrkntXj30gdStYmAPj+bHdHBGFdRUpUCb7rboyCGt2607kDVqbqONUFzwtC2vDu7MX4GdE8qDdeqMoP8XGAygQ+QeFjZX/IZNZTwMKYw3RIwZN/2Px\",\"iterations\":1000000,\"iv\":\"rh++GLYnLzQppaUgCZKPr9Lcp8EWm6ZEka5dCqo74Jg=\"}";

        JsonElement jsonElement = JsonParser.parseString(jwk);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String nonce = jsonObject.get("nonce").getAsString();
        String wkey = jsonObject.get("wkey").getAsString();
        int iterations = jsonObject.get("iterations").getAsInt();
        String iv = jsonObject.get("iv").getAsString();

        ByteBuffer byteBuffer = ByteBuffer.allocate(Tools.toBytes(iv).length + Tools.toBytes(wkey).length);
        byteBuffer.put(Tools.toBytes(iv));
        byteBuffer.put(Tools.toBytes(wkey));

        SecretKey secretKey = Tools.getSecretKeyPBKDF2("0000".toCharArray(), Tools.toBytes(nonce), iterations);

        byte[] output = Cipher.decipher(secretKey, byteBuffer.array());

        System.out.println(new String(output));

        JsonElement jsonElement1 = JsonParser.parseString(new String(output));
        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();

        BigInteger p = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
        ECFieldFp field = new ECFieldFp(p);
        BigInteger a = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948");
        BigInteger b = new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291");
        BigInteger n = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");
        int h = 1;
        EllipticCurve curve = new EllipticCurve(field, a, b);

        ECPoint ecPoint = new ECPoint(new BigInteger(Tools.toBytes(jsonObject1.get("x").getAsString())), new BigInteger(Tools.toBytes(jsonObject1.get("y").getAsString())));

        ECParameterSpec ecParameterSpec = new ECParameterSpec(curve, ecPoint, n, h);

        ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        System.out.println(Tools.toBase64(publicKey.getEncoded()));
    }

    @Test
    public void testJWK2() throws GeneralSecurityException {
        MyKeyPair myKeyPair = new MyKeyPair();
        String outputJson = myKeyPair.exportToJSON(Tools.hashPassword("0000"));

        System.out.println(outputJson);

        JsonElement jsonElement = JsonParser.parseString(outputJson);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String nonce = jsonObject.get("nonce").getAsString();
        String wkey = jsonObject.get("wkey").getAsString();
        int iterations = jsonObject.get("iterations").getAsInt();
        String iv = jsonObject.get("iv").getAsString();

        ByteBuffer byteBuffer = ByteBuffer.allocate(Tools.toBytes(iv).length + Tools.toBytes(wkey).length);
        byteBuffer.put(Tools.toBytes(iv));
        byteBuffer.put(Tools.toBytes(wkey));

        SecretKey secretKey = Tools.getSecretKeyPBKDF2(Tools.hashPassword("0000").toCharArray(), Tools.toBytes(nonce), iterations);

        byte[] output = Cipher.decipher(secretKey, byteBuffer.array());

        System.out.println(new String(output));
    }

    @Test
    public void testPBKDF2() throws GeneralSecurityException {
        String test = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==";

        byte[] salt = Tools.toBytes("cKpdYGz6EXp5QctsIOsmVSn7ewGwwx5fW8TdA7N+5Ho=");
        SecretKey totoSecretKeyPBKDF2 = Tools.getSecretKeyPBKDF2("toto".toCharArray(), salt, 1000);
        byte[] output = Cipher.cipher(totoSecretKeyPBKDF2, test.getBytes(StandardCharsets.UTF_8));

        assertEquals("ViK7x2IDuqGu13hNAdjMRXaDmu0nSj93KPt1UvylebE=", Tools.toBase64(totoSecretKeyPBKDF2.getEncoded()));
        assertEquals(test, new String(Cipher.decipher(totoSecretKeyPBKDF2, output)));


        byte[] salt2 = Tools.generateRandomBytes(32);
        SecretKey secretKeyPBKDF2 = Tools.getSecretKeyPBKDF2("1234".toCharArray(), salt2, Tools.PBKDF2_ITERATION);
        byte[] output2 = Cipher.cipher(secretKeyPBKDF2, test.getBytes(StandardCharsets.UTF_8));

        assertEquals(test, new String(Cipher.decipher(secretKeyPBKDF2, output2)));
        assertThrows(AEADBadTagException.class, () -> Cipher.decipher(totoSecretKeyPBKDF2, output2));
    }

    @Test
    public void testAll() throws GeneralSecurityException, IOException {
        //Create MyState
        String hashedPassword = Tools.hashPassword("1234");
        SecretKey secretKey = Tools.getSecretKeyPBKDF2(hashedPassword.toCharArray(), Tools.generateRandomBytes(32), 1024);
        MyState myStateUser1 = MyState.load(hashedPassword, secretKey);

        myStateUser1.incrementMyNonce();
        myStateUser1.save();

        SecretKey newSecretKey = Tools.loadSecretKey(hashedPassword);
        MyState myStateFile = MyState.load(hashedPassword, newSecretKey);

        assertArrayEquals(myStateUser1.getMyPublicKey().getEncoded(), myStateFile.getMyPublicKey().getEncoded());
        assertArrayEquals(myStateUser1.getMyPrivateKey().getEncoded(), myStateFile.getMyPrivateKey().getEncoded());
        assertEquals(myStateUser1.getMyNonce(), myStateFile.getMyNonce());

        //Create Conversation
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, myStateUser1.getMyNonce());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce());

        user2.getMyDirectory().addPerson("user1", myStateUser1.getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(message1User2, message1User1For2);

        myStateUser1.getMyDirectory().addPerson("user2", user2.getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(message1User1, message1User2For1);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));

        String message2User1 = Communication.createMessage2(myStateUser1.getMyPrivateKey(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getMyPrivateKey(), secretBuildUser2);

        assertEquals("user1", Communication.handleMessage2(user2.getMyDirectory(), secretBuildUser2, message2User1));
        assertEquals("user2", Communication.handleMessage2(myStateUser1.getMyDirectory(), secretBuildUser1, message2User2));
        //End create Conversation

        //Add Conversation to MyState
        myStateUser1.addAConversation(secretBuildUser1);
        myStateUser1.incrementMyNonce();
        myStateUser1.save();

        assertEquals(1, myStateUser1.getMyConversations().getSize());

        MyConversations myConversationsUser1 = myStateUser1.getMyConversations();

        //Test cipher/decipher message
        String textString = "Another bites the dust";
        byte[] cipheredTextUser1 = Cipher.cipher(Tools.toSecretKey(myConversationsUser1.getConversation(0).getSymKey()), textString.getBytes(StandardCharsets.UTF_8));
        String cipheredTextBase64User1 = Tools.toBase64(cipheredTextUser1);

        byte[] cipheredTextFromUser1 = Tools.toBytes(cipheredTextBase64User1);
        assertEquals(textString, new String(Cipher.decipher(Tools.toSecretKey(secretBuildUser2.getSymKey()), cipheredTextFromUser1)));

        //Delete a Conversation
        myConversationsUser1.deleteConversation(myConversationsUser1.getConversation(0));
        myStateUser1.save();

        assertEquals(0, myConversationsUser1.getSize());
    }
}
