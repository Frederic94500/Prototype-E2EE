package fr.upec.Prototype_E2EE;

import org.junit.BeforeClass;
import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {
    static private KeyPair user1;
    static private KeyPair user2;

    @BeforeClass
    public static void setupClass() {
        user1 = Keys.generate();
        user2 = Keys.generate();
    }

    @Test
    public void testMessage1() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        Message1 message1User1 = new Message1(Tools.toBase64(user1.getPublic().getEncoded()), 10, System.currentTimeMillis() / 1000L);
        String message1User1String = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2, message1User1String);

        assertEquals(message1User1.getPubKey(), secretBuildUser2.getOtherPubKey());
        assertEquals(message1User1.getNonce(), secretBuildUser2.getOtherNonce());
        assertEquals(message1User1.getTimestamp(), secretBuildUser2.getOtherDate());
    }

    @Test
    public void testMessage2() throws Exception {
        Message1 message1User1 = new Message1(Tools.toBase64(user1.getPublic().getEncoded()), 10, System.currentTimeMillis() / 1000L);
        String message1User1String = Communication.createMessage1(message1User1);
        SecretBuild secretBuildUser2 = Communication.handleMessage1(user2, message1User1String);

        Message1 message1User2 = new Message1(Tools.toBase64(user2.getPublic().getEncoded()), 10, System.currentTimeMillis() / 1000L);
        String message1User2String = Communication.createMessage1(message1User2);
        SecretBuild secretBuildUser1 = Communication.handleMessage1(user1, message1User2String);

        assertTrue(secretBuildUser1.equals(secretBuildUser2));

        String message2User1 = Communication.createMessage2(user1.getPrivate(), secretBuildUser1);
        String message2User2 = Communication.createMessage2(user2.getPrivate(), secretBuildUser2);

        assertTrue(Communication.handleMessage2(secretBuildUser2, message2User1));
        assertTrue(Communication.handleMessage2(secretBuildUser1, message2User2));
    }

    @Test
    public void testSigning() {

    }

    @Test
    public void testVerifySigning() {

    }
}
