package fr.upec.Prototype_E2EE;

import fr.upec.Prototype_E2EE.MyState.MyKeyPair;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    static private KeyPair user1;
    static private KeyPair user2;
    static private SecretBuild sbUser1;
    static private SecretBuild sbUser2;

    @BeforeClass
    public static void setupClass() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        user1 = Keys.generate();
        user2 = Keys.generate();
    }

    @Test
    public void testMessage1() throws GeneralSecurityException {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, 10, user1.getPublic().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, 100, user2.getPublic().getEncoded());

        String message1User1String = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2.getPrivate(), user2.getPublic(), message1User2, message1User1String);

        String message1User2String = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(user1.getPrivate(), user1.getPublic(), message1User1, message1User2String);

        assertEquals(message1User1.getTimestamp(), secretBuildUser2.getOtherDate());
        assertEquals(message1User1.getNonce(), secretBuildUser2.getOtherNonce());
        assertArrayEquals(message1User1.getPubKey(), secretBuildUser2.getOtherPubKey());

        assertEquals(message1User2.getTimestamp(), secretBuildUser1.getOtherDate());
        assertEquals(message1User2.getNonce(), secretBuildUser1.getOtherNonce());
        assertArrayEquals(message1User2.getPubKey(), secretBuildUser1.getOtherPubKey());
    }

    @Test
    public void testMessage2() throws Exception {
        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, 10, user1.getPublic().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, 100, user2.getPublic().getEncoded());

        String message1User1String = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2.getPrivate(), user2.getPublic(), message1User2, message1User1String);

        String message1User2String = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(user1.getPrivate(), user1.getPublic(), message1User1, message1User2String);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));
        sbUser1 = secretBuildUser1;
        sbUser2 = secretBuildUser2;

        String message2User1 = Communication.createMessage2(user1.getPrivate(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getPrivate(), secretBuildUser2);

        assertTrue(Communication.handleMessage2(secretBuildUser2, message2User1));
        assertTrue(Communication.handleMessage2(secretBuildUser1, message2User2));
    }

    @Test
    public void testSigningVerifying() throws GeneralSecurityException {
        String textString = "Around the World, Around the World";
        byte[] signatureUser1 = Sign.sign(user1.getPrivate(), textString);
        String signatureBase64User1 = Tools.toBase64(signatureUser1);

        byte[] signatureFromUser1 = Tools.toBytes(signatureBase64User1);
        assertTrue(Sign.verify(user1.getPublic(), signatureFromUser1, textString));
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
    public void testSaveAndLoadMyKeyPair() throws GeneralSecurityException, FileNotFoundException {
        MyKeyPair myKeyPair = MyKeyPair.load(); //Without File
        MyKeyPair myKeyPairViaFile = MyKeyPair.load(); //With File

        assertArrayEquals(myKeyPair.getMyPublicKey().getEncoded(), myKeyPairViaFile.getMyPublicKey().getEncoded());
        assertArrayEquals(myKeyPair.getMyPrivateKey().getEncoded(), myKeyPairViaFile.getMyPrivateKey().getEncoded());

        Tools.deleteFile(MyKeyPair.filename);
    }

    @Test
    public void testSaveAndLoadMyInformation() throws GeneralSecurityException, IOException {
        MyState myState = new MyState();
        myState.save();

        MyState myStateFile = MyState.load();

        assertArrayEquals(myState.getMyKeyPair().getMyPublicKey().getEncoded(), myStateFile.getMyKeyPair().getMyPublicKey().getEncoded());
        assertArrayEquals(myState.getMyKeyPair().getMyPrivateKey().getEncoded(), myStateFile.getMyKeyPair().getMyPrivateKey().getEncoded());
        assertEquals(myState.getMyNonce(), myStateFile.getMyNonce());

        Tools.deleteFile(MyState.filename);
    }

    @Test
    public void testAll() throws GeneralSecurityException, IOException {
        //Create MyState
        MyState myStateUser1 = new MyState();

        myStateUser1.incrementMyNonce();
        myStateUser1.save();

        MyState myStateFile = MyState.load();

        assertArrayEquals(myStateUser1.getMyKeyPair().getMyPublicKey().getEncoded(), myStateFile.getMyKeyPair().getMyPublicKey().getEncoded());
        assertArrayEquals(myStateUser1.getMyKeyPair().getMyPrivateKey().getEncoded(), myStateFile.getMyKeyPair().getMyPrivateKey().getEncoded());
        assertEquals(myStateUser1.getMyNonce(), myStateFile.getMyNonce());

        //Create Conversation
        int nonceUser2 = 0;

        Message1 message1User1 = new Message1(System.currentTimeMillis() / 1000L, myStateUser1.getMyNonce(), myStateUser1.getMyKeyPair().getMyPublicKey().getEncoded());
        Message1 message1User2 = new Message1(System.currentTimeMillis() / 1000L, nonceUser2, user2.getPublic().getEncoded());

        String message1User1String = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2.getPrivate(), user2.getPublic(), message1User2, message1User1String);

        String message1User2String = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(myStateUser1.getMyKeyPair().getMyPrivateKey(), myStateUser1.getMyKeyPair().getMyPublicKey(), message1User1, message1User2String);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));

        String message2User1 = Communication.createMessage2(myStateUser1.getMyKeyPair().getMyPrivateKey(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getPrivate(), secretBuildUser2);

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

        Tools.deleteFile(MyState.filename);
        Tools.deleteFile(MyKeyPair.filename);
    }
}
