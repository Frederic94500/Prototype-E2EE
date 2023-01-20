package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.Communication;
import fr.upec.Prototype_E2EE.Protocol.Message1;
import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

/**
 * Start a new Conversation Menu for CLI
 */
public class StartConversationMenu implements InterfaceCLI {
    /**
     * Menu for the Message 1
     *
     * @param scanner User input
     * @param myState User information
     * @return Return ToMessage2
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     * @throws IOException              Throws IOException if there is an I/O exception
     */
    private SecretBuild message1(Scanner scanner, MyState myState) throws GeneralSecurityException, IOException {
        Message1 myMessage1 = new Message1(Tools.getCurrentTime(), myState.getMyNonce(), myState.getMyPublicKey().getEncoded());
        myState.incrementMyNonce();
        myState.save();
        System.out.println("Please copy the Message 1 and transfer to your recipient:");
        System.out.println(Communication.createMessage1(myMessage1) + "\n");

        String inputOtherMessage1;
        SecretBuild secretBuild = null;
        do {
            inputOtherMessage1 = Tools.getInput(scanner, "Please paste the Message 1 from your sender (0 = return back): \n");
            if (inputOtherMessage1.equals("0")) {
                return null;
            }
            try {
                secretBuild = Communication.handleMessage1(myState, myMessage1, inputOtherMessage1);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (secretBuild == null);

        return secretBuild;
    }

    /**
     * Menu for the Message 2
     *
     * @param scanner       User input
     * @param myState       User information
     * @param mySecretBuild Information for a conversation
     * @return Return a boolean if it is correct
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    private boolean message2(Scanner scanner, MyState myState, SecretBuild mySecretBuild) throws GeneralSecurityException {
        String myMessage2 = Communication.createMessage2(myState.getMyPrivateKey(), mySecretBuild);
        System.out.println("Please copy the Message 2 and transfer to your recipient: ");
        System.out.println(myMessage2 + "\n");

        String input;
        boolean pass = false;
        do {
            input = Tools.getInput(scanner, "Please paste the Message 2 from your sender (0 = return back): \n");
            if (input.equals("0")) {
                return false;
            } else {
                try {
                    pass = Communication.handleMessage2(mySecretBuild, input);
                    pass = true;
                } catch (Exception e) {
                    System.out.println("This is not the expected Message 2!");
                }
            }
        } while (!pass);

        return true;
    }

    /**
     * Menu to start a Conversation
     *
     * @param scanner User input
     * @param myState User information
     * @throws IOException              Throws IOException if there is an I/O exception
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    @Override
    public void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException {
        SecretBuild message2 = message1(scanner, myState);
        if (message2 != null) {
            System.out.println();
            if (message2(scanner, myState, message2)) {
                System.out.println();
                myState.addAConversation(message2);
                myState.save();

                System.out.println("Conversation with " + myState.getMyDirectory().getKeyName(message2.getOtherPubKey()) + " has been created!\n");
            }
        }
    }
}
