package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Main Menu for CLI
 */
public class MainMenu implements InterfaceCLI {
    /**
     * Main Menu for CLI
     *
     * @param scanner Scanner for user input
     * @param myState User Information
     */
    @Override
    public void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException {
        boolean cli = true;
        int input;
        HashMap<Integer, InterfaceCLI> commands = new HashMap<>();

        commands.put(1, new StartConversationMenu());
        commands.put(3, new MyIdentityMenu());
        commands.put(4, new MyDirectoryMenu());
        while (cli) {
            System.out.println("""
                    ========== Main Menu ==========
                    | 0 - Quit
                    | 1 - Create a new conversation
                    | 2 - My conversations
                    | 3 - My identity
                    | 4 - My directory
                    ===============================""");
            input = Tools.getInput(scanner, /*commands.size()*/ 4);

            if (input == 0) {
                cli = false;
            } else {
                commands.get(input).menu(scanner, myState);
            }
        }

        System.out.println("Bye");
        scanner.close();
    }
}
