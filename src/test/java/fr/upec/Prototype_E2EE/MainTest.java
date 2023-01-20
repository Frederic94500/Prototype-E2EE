package fr.upec.Prototype_E2EE;

import fr.upec.Prototype_E2EE.MyState.MyDirectory;
import fr.upec.Prototype_E2EE.MyState.MyKeyPair;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    static private MyState user1;
    static private MyState user2;
    static private SecretBuild sbUser1;
    static private SecretBuild sbUser2;

    @BeforeClass
    public static void setupClass() throws GeneralSecurityException, IOException {
        user1 = new MyState();
        user2 = new MyState();
    }

    @Before
    public void deleteFilesBefore() {
        Tools.deleteFile(MyState.filename);
        Tools.deleteFile(MyKeyPair.filename);
        Tools.deleteFile(MyDirectory.filename);
    }

    @After
    public void deleteFilesAfter() {
        Tools.deleteFile(MyState.filename);
        Tools.deleteFile(MyKeyPair.filename);
        Tools.deleteFile(MyDirectory.filename);
    }

    @Test
    public void testMessage1() throws GeneralSecurityException {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, user1.getMyNonce(), user1.getMyKeyPair().getMyPublicKey().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce(), user2.getMyKeyPair().getMyPublicKey().getEncoded());

        user2.getMyDirectory().addPerson("user1", user1.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2, message1User2, message1User1For2);

        user1.getMyDirectory().addPerson("user2", user2.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(user1, message1User1, message1User2For1);

        assertEquals(message1User1.getTimestamp(), secretBuildUser2.getOtherDate());
        assertEquals(message1User1.getNonce(), secretBuildUser2.getOtherNonce());
        assertArrayEquals(message1User1.getPubKey(), secretBuildUser2.getOtherPubKey());

        assertEquals(message1User2.getTimestamp(), secretBuildUser1.getOtherDate());
        assertEquals(message1User2.getNonce(), secretBuildUser1.getOtherNonce());
        assertArrayEquals(message1User2.getPubKey(), secretBuildUser1.getOtherPubKey());
    }

    @Test
    public void testMessage2() throws Exception {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, user1.getMyNonce(), user1.getMyKeyPair().getMyPublicKey().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce(), user2.getMyKeyPair().getMyPublicKey().getEncoded());

        user2.getMyDirectory().addPerson("user1", user1.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2, message1User2, message1User1For2);

        user1.getMyDirectory().addPerson("user2", user2.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(user1, message1User1, message1User2For1);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));
        sbUser1 = secretBuildUser1;
        sbUser2 = secretBuildUser2;

        String message2User1 = Communication.createMessage2(user1.getMyKeyPair().getMyPrivateKey(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getMyKeyPair().getMyPrivateKey(), secretBuildUser2);

        assertTrue(Communication.handleMessage2(secretBuildUser2, message2User1));
        assertTrue(Communication.handleMessage2(secretBuildUser1, message2User2));
    }

    @Test
    public void testSigningVerifying() throws GeneralSecurityException {
        String textString = "Around the World, Around the World";
        byte[] signatureUser1 = Sign.sign(user1.getMyKeyPair().getMyPrivateKey(), textString);
        String signatureBase64User1 = Tools.toBase64(signatureUser1);

        byte[] signatureFromUser1 = Tools.toBytes(signatureBase64User1);
        assertTrue(Sign.verify(user1.getMyKeyPair().getMyPublicKey(), signatureFromUser1, textString));
    }

    @Test
    public void testCipherDecipher() throws GeneralSecurityException {
        String textString = "Moeagare Moeagare GANDAMU!";
        byte[] cipheredTextUser1 = MessageCipher.cipher(Tools.toSecretKey(sbUser1.getSymKey()), textString.getBytes(StandardCharsets.UTF_8));
        String cipheredTextBase64User1 = Tools.toBase64(cipheredTextUser1);

        byte[] cipheredTextFromUser1 = Tools.toBytes(cipheredTextBase64User1);
        assertEquals(textString, new String(MessageCipher.decipher(Tools.toSecretKey(sbUser2.getSymKey()), cipheredTextFromUser1)));
    }

    @Test
    public void testSaveAndLoadMyKeyPair() throws GeneralSecurityException, IOException {
        MyKeyPair myKeyPair = MyKeyPair.load(); //Without File
        myKeyPair.save();
        MyKeyPair myKeyPairViaFile = MyKeyPair.load(); //With File

        assertArrayEquals(myKeyPair.getMyPublicKey().getEncoded(), myKeyPairViaFile.getMyPublicKey().getEncoded());
        assertArrayEquals(myKeyPair.getMyPrivateKey().getEncoded(), myKeyPairViaFile.getMyPrivateKey().getEncoded());
    }

    @Test
    public void testSaveAndLoadMyState() throws GeneralSecurityException, IOException {
        MyState myState = new MyState();
        myState.save();

        MyState myStateFile = MyState.load();

        assertArrayEquals(myState.getMyKeyPair().getMyPublicKey().getEncoded(), myStateFile.getMyKeyPair().getMyPublicKey().getEncoded());
        assertArrayEquals(myState.getMyKeyPair().getMyPrivateKey().getEncoded(), myStateFile.getMyKeyPair().getMyPrivateKey().getEncoded());
        assertEquals(myState.getMyNonce(), myStateFile.getMyNonce());
    }

    @Test
    public void testMyDirectory() throws IOException {
        MyDirectory myDirectory = new MyDirectory();
        assertEquals(0, myDirectory.sizeOfDirectory());

        myDirectory.addPerson("user1", user1.getMyKeyPair().getMyPublicKey().getEncoded());
        assertEquals(1, myDirectory.sizeOfDirectory());

        myDirectory.addPerson("user2", user2.getMyKeyPair().getMyPublicKey().getEncoded());
        assertEquals(2, myDirectory.sizeOfDirectory());

        myDirectory.saveIntoFile();
        MyDirectory myDirectoryFile = new MyDirectory();

        assertTrue(myDirectory.isInDirectory(user1.getMyKeyPair().getMyPublicKey().getEncoded()));
        assertTrue(myDirectoryFile.isInDirectory(myDirectory.getPerson("user2")));

        myDirectory.deletePerson("user2");
        assertEquals(1, myDirectory.sizeOfDirectory());
        myDirectory.saveIntoFile();

        MyDirectory myDirectoryFile2 = new MyDirectory();
        assertEquals(1, myDirectoryFile2.sizeOfDirectory());
    }

    @Test
    public void testReplaceMyKeyPair() throws GeneralSecurityException, IOException {
        MyState myState = new MyState();
        myState.save();
        MyState myStateFile = MyState.load();
        String digestFile = Tools.digest(MyKeyPair.filename);

        myState.replaceMyKeyPair();

        assertFalse(Arrays.equals(myStateFile.getMyKeyPair().getMyPublicKey().getEncoded(), myState.getMyKeyPair().getMyPublicKey().getEncoded()));
        assertNotEquals(digestFile, Tools.digest(MyKeyPair.filename));
    }

    @Test
    public void testParserPubKey() {
        String pemKey = "-----BEGIN EC PUBLIC KEY-----MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==-----END EC PUBLIC KEY-----";
        String pubKey = Tools.keyParser(pemKey);
        String reTestPubKey = Tools.keyParser(pubKey);
        String wrongPubKey = "I'm sorry, Dave. I'm afraid I can't do that.";

        assertEquals("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==", pubKey);
        assertEquals("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAECdQQzt/cpVAylBBPo4qw6dwVU17vNy5ZQG9QJqUwZnnC4yMjdrFC0MIvPgGxA/p1yOLPbSXnQZKEak27u9OEZg==", reTestPubKey);
        assertThrows(IllegalArgumentException.class, () -> Tools.keyParser(wrongPubKey));
    }

    @Test
    public void testAll() throws GeneralSecurityException, IOException {
        //Create MyState
        MyState myStateUser1 = MyState.load();

        myStateUser1.incrementMyNonce();
        myStateUser1.save();

        MyState myStateFile = MyState.load();

        assertArrayEquals(myStateUser1.getMyKeyPair().getMyPublicKey().getEncoded(), myStateFile.getMyKeyPair().getMyPublicKey().getEncoded());
        assertArrayEquals(myStateUser1.getMyKeyPair().getMyPrivateKey().getEncoded(), myStateFile.getMyKeyPair().getMyPrivateKey().getEncoded());
        assertEquals(myStateUser1.getMyNonce(), myStateFile.getMyNonce());

        //Create Conversation
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, myStateUser1.getMyNonce(), myStateUser1.getMyKeyPair().getMyPublicKey().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, user2.getMyNonce(), user2.getMyKeyPair().getMyPublicKey().getEncoded());

        user2.getMyDirectory().addPerson("user1", myStateUser1.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User1For2 = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2, message1User2, message1User1For2);

        myStateUser1.getMyDirectory().addPerson("user2", user2.getMyKeyPair().getMyPublicKey().getEncoded());
        String message1User2For1 = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(myStateUser1, message1User1, message1User2For1);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));

        String message2User1 = Communication.createMessage2(myStateUser1.getMyKeyPair().getMyPrivateKey(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getMyKeyPair().getMyPrivateKey(), secretBuildUser2);

        assertTrue(Communication.handleMessage2(secretBuildUser2, message2User1));
        assertTrue(Communication.handleMessage2(secretBuildUser1, message2User2));
        //End create Conversation

        //Add Conversation to MyInformation
        myStateUser1.addAConversation(secretBuildUser1);
        myStateUser1.incrementMyNonce();
        myStateUser1.save();

        assertEquals(1, myStateUser1.getMyConversations().size());

        //Test cipher/decipher message
        String textString = "Another bites the dust";
        byte[] cipheredTextUser1 = MessageCipher.cipher(Tools.toSecretKey(myStateUser1.getMyConversations().get(0).getSymKey()), textString.getBytes(StandardCharsets.UTF_8));
        String cipheredTextBase64User1 = Tools.toBase64(cipheredTextUser1);

        byte[] cipheredTextFromUser1 = Tools.toBytes(cipheredTextBase64User1);
        assertEquals(textString, new String(MessageCipher.decipher(Tools.toSecretKey(secretBuildUser2.getSymKey()), cipheredTextFromUser1)));

        //Delete a Conversation
        myStateUser1.deleteAConversation(myStateUser1.getMyConversations().get(0).getMyNonce());
        myStateUser1.save();

        assertEquals(0, myStateUser1.getMyConversations().size());
    }
}
