package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyConversations;
import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.MessageCipher;
import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

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
     * @param secretBuild     The chosen conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void conversationMenu(Scanner scanner, MyConversations myConversations, SecretBuild secretBuild) throws GeneralSecurityException {
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
                cipherMenu(scanner, secretBuild);
            } else if (input == 2) {
                decipherMenu(scanner, secretBuild);
            } else if (input == 3) {
                myConversations.deleteConversation(secretBuild);
                cli = false;
            }
        } while (cli);
    }

    /**
     * Cipher a message
     *
     * @param scanner     User input
     * @param secretBuild A conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void cipherMenu(Scanner scanner, SecretBuild secretBuild) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(secretBuild.getSymKey());

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
     * @param scanner     User input
     * @param secretBuild A conversation
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private void decipherMenu(Scanner scanner, SecretBuild secretBuild) throws GeneralSecurityException {
        boolean cli = true;
        String input;
        SecretKey secretKey = Tools.toSecretKey(secretBuild.getSymKey());

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
                    SecretBuild conversation = myConversations.getConversation(i);
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
                conversationMenu(scanner, myConversations, myConversations.getConversation(input - 1));
            }
        } while (cli);
    }
}
