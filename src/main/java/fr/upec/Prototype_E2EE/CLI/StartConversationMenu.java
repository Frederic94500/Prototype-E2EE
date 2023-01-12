package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Protocol.Communication;
import fr.upec.Prototype_E2EE.Protocol.Message1;
import fr.upec.Prototype_E2EE.Protocol.SecretBuild;
import fr.upec.Prototype_E2EE.Tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

class ToMessage2 {
    boolean toMessage2;
    SecretBuild secretBuild;

    public ToMessage2(boolean toMessage2, SecretBuild secretBuild) {
        this.toMessage2 = toMessage2;
        this.secretBuild = secretBuild;
    }
}

public class StartConversationMenu implements InterfaceCLI {
    private ToMessage2 message1(Scanner scanner, MyState myState) throws GeneralSecurityException, IOException {
        Message1 myMessage1 = new Message1(Tools.getCurrentTime(), myState.getMyNonce(), myState.getMyKeyPair().getMyPublicKey().getEncoded());
        myState.incrementMyNonce();
        myState.save();
        System.out.println("Please copy the Message 1 and transfer to your recipient:");
        System.out.println(Communication.createMessage1(myMessage1) + "\n");

        String inputOtherMessage1;
        SecretBuild secretBuild = null;
        do {
            inputOtherMessage1 = Tools.getInput(scanner, "Please paste the Message 1 from your sender (0 = return back): \n");
            if (inputOtherMessage1.equals("00")) {
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

    private boolean message2(Scanner scanner, MyState myState, SecretBuild mySecretBuild) throws GeneralSecurityException {
        String myMessage2 = Communication.createMessage2(myState.getMyKeyPair().getMyPrivateKey(), mySecretBuild);
        System.out.println("Please copy the Message 2 and transfer to your recipient: ");
        System.out.println(myMessage2 + "\n");

        String input;
        do {
            input = Tools.getInput(scanner, "Please paste the Message 2 from your sender (0 = return back): \n");
            if (input.equals("00")) {
                return false;
            }
        } while (!Communication.handleMessage2(mySecretBuild, input));

        return true;
    }

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
