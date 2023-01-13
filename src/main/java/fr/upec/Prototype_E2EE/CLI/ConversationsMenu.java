package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyConversation;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.MessageCipher;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Scanner;

/**
 * Conversation Menu for CLI
 */
public class ConversationsMenu implements InterfaceCLI {
    /**
     * Menu for a single conversation
     *
     * @param scanner        User input
     * @param myState        User information
     * @param myConversation A conversation
     */
    private void conversationMenu(Scanner scanner, MyState myState, MyConversation myConversation) throws GeneralSecurityException, IOException {
        boolean cli = true;
        int input;

        do {
            System.out.println("""
                    ========== Conversation Menu ==========
                    | 0 - Return back
                    | 1 - Cipher a message
                    | 2 - Decipher a message
                    | 3 - Delete the conversation
                    ===============================""");
            input = Tools.getInput(scanner, 3);

            if (input == 0) {
                cli = false;
            } else if (input == 1) {
                cipherMenu(scanner, myConversation);
            } else if (input == 2) {
                decipherMenu(scanner, myConversation);
            } else if (input == 3) {
                myState.deleteAConversation(myConversation);
                cli = false;
            }
        } while (cli);
    }

    /**
     * Cipher a message
     *
     * @param scanner        User input
     * @param myConversation A conversation
     */
    private void cipherMenu(Scanner scanner, MyConversation myConversation) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(myConversation.getSymKey());

        do {
            input = Tools.getInput(scanner, "Please type your message to cipher (0 = return back):\n");
            if (input.equals("0")) {
                cli = false;
            } else {
                byte[] cipheredMessageByte = MessageCipher.cipher(secretKey, input.getBytes(StandardCharsets.UTF_8));
                System.out.println("Please copy and send the ciphered message to your receiver");
                System.out.println(Tools.toBase64(cipheredMessageByte) + "\n");
            }
        } while (cli);
    }

    /**
     * Decipher a message
     *
     * @param scanner        User input
     * @param myConversation A conversation
     */
    private void decipherMenu(Scanner scanner, MyConversation myConversation) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(myConversation.getSymKey());

        do {
            input = Tools.getInput(scanner, "Please paste the message to decipher (0 = return back):\n");
            if (input.equals("0")) {
                cli = false;
            } else {
                byte[] decipheredMessageByte = MessageCipher.decipher(secretKey, Tools.toBytes(input));
                System.out.println("Here is your deciphered message:");
                System.out.println(new String(decipheredMessageByte) + "\n");
            }
        } while (cli);
    }

    /**
     * Menu to choose a conversation
     *
     * @param scanner User input
     * @param myState User information
     */
    @Override
    public void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException {
        boolean cli = true;
        int input;

        do {
            StringBuilder sb = new StringBuilder();
            sb.append("========== Conversations Menu ==========\n");
            sb.append("| 0 - Return back\n");
            if (myState.getConversationSize() == 0) {
                sb.append("== You do not have any conversations! ==\n");
            } else {
                sb.append("===== Please choose a conversation =====\n");
                for (int i = 0; i < myState.getConversationSize(); i++) {
                    MyConversation conversation = myState.getConversation(i);
                    Date date = new Date(conversation.getMyDate() * 1000L);
                    sb.append("| ")
                            .append(i + 1)
                            .append(" - ")
                            .append(myState.getMyDirectory().getKeyName(conversation.getOtherPubKey()))
                            .append(" - ")
                            .append(date)
                            .append("\n");
                }
            }
            sb.append("========================================");
            System.out.println(sb);
            input = Tools.getInput(scanner, myState.getConversationSize());

            if (input == 0) {
                cli = false;
            } else {
                conversationMenu(scanner, myState, myState.getConversation(input - 1));
            }
        } while (cli);
    }
}
