package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyConversations;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.Cipher;
import fr.upec.Prototype_E2EE.Protocol.Conversation;
import fr.upec.Prototype_E2EE.Tools;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
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
     * @param scanner         User input
     * @param myConversations All User conversations
     * @param conversation    The chosen conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void conversationMenu(Scanner scanner, MyConversations myConversations, Conversation conversation) throws GeneralSecurityException {
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
                cipherMenu(conversation);
            } else if (input == 2) {
                decipherMenu(scanner, conversation);
            } else if (input == 3) {
                myConversations.deleteConversation(conversation);
                System.out.println("This conversation has been deleted!\n");
                cli = false;
            }
        } while (cli);
    }

    /**
     * Cipher a message
     *
     * @param conversation A conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void cipherMenu(Conversation conversation) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(conversation.getSecretKey());

        do {
            input = Tools.getInput("Please type your message to cipher (0 = return back):\n");
            if (input.equals("0")) {
                cli = false;
            } else {
                byte[] cipheredMessageByte = Cipher.cipher(secretKey, input.getBytes(StandardCharsets.UTF_8));
                System.out.println("Please copy and send the ciphered message to your receiver");
                System.out.println(Tools.toBase64(cipheredMessageByte) + "\n");
            }
        } while (cli);
    }

    /**
     * Decipher a message
     *
     * @param scanner      User input
     * @param conversation A conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void decipherMenu(Scanner scanner, Conversation conversation) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(conversation.getSecretKey());

        do {
            try {
                input = Tools.getInput(scanner, "Please paste the message to decipher (0 = return back):\n");
                if (input.equals("0")) {
                    cli = false;
                } else {
                    byte[] decipheredMessageByte = Cipher.decipher(secretKey, Tools.toBytes(input));
                    System.out.println("Here is your deciphered message:");
                    System.out.println(new String(decipheredMessageByte) + "\n");
                }
            } catch (IllegalArgumentException | AEADBadTagException e) {
                System.out.println("Cannot decipher this message!");
            }
        } while (cli);
    }

    /**
     * Menu to choose a conversation
     *
     * @param scanner User input
     * @param myState User information
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    @Override
    public void menu(Scanner scanner, MyState myState) throws GeneralSecurityException {
        boolean cli = true;
        int input;
        MyConversations myConversations = myState.getMyConversations();

        do {
            StringBuilder sb = new StringBuilder();
            sb.append("========== Conversations Menu ==========\n");
            sb.append("| 0 - Return back\n");
            if (myState.getConversationSize() == 0) {
                sb.append("== You do not have any conversations! ==\n");
            } else {
                sb.append("===== Please choose a conversation =====\n");
                for (int i = 0; i < myConversations.getSize(); i++) {
                    Conversation conversation = myConversations.getConversation(i);
                    Date date = new Date(conversation.getDate() * 1000L);
                    String name = myConversations.getConversation(i).getName();
                    sb.append("| ")
                            .append(i + 1)
                            .append(" - ")
                            .append(name)
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
                conversationMenu(scanner, myConversations, myConversations.getConversation(input - 1));
            }
        } while (cli);
    }
}
