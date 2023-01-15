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
 * ToMessage2 is a temporary class to return 2 objects at a time
 */
class ToMessage2 {
    boolean toMessage2;
    SecretBuild secretBuild;

    /**
     * Constructor of ToMessage2
     *
     * @param toMessage2  To pass to the Message2
     * @param secretBuild SecretBuild for the conversation
     */
    public ToMessage2(boolean toMessage2, SecretBuild secretBuild) {
        this.toMessage2 = toMessage2;
        this.secretBuild = secretBuild;
    }
}

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
    private ToMessage2 message1(Scanner scanner, MyState myState) throws GeneralSecurityException, IOException {
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
                return new ToMessage2(false, null);
            }
            try {
                secretBuild = Communication.handleMessage1(myState, myMessage1, inputOtherMessage1);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (secretBuild == null);

        return new ToMessage2(true, secretBuild);
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
        ToMessage2 toMessage2 = message1(scanner, myState);
        if (toMessage2.toMessage2) {
            System.out.println();
            if (message2(scanner, myState, toMessage2.secretBuild)) {
                System.out.println();
                myState.addAConversation(toMessage2.secretBuild);
                myState.save();

                System.out.println("Conversation with " + myState.getMyDirectory().getKeyName(toMessage2.secretBuild.getOtherPubKey()) + " has been created!\n");
            }
        }
    }
}
