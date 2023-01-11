package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu implements InterfaceCLI {
    public void menu(Scanner scanner, MyState myState) {
        boolean cli = true;
        boolean typing;
        int input = 0;
        HashMap<Integer, InterfaceCLI> commands = new HashMap<>();

        commands.put(1, new MenuMyIdentity());
        while (cli) {
            typing = true;
            System.out.println("""
                    ========== Main Menu ==========
                    | 0 - Quit
                    | 1 - Create a new conversation
                    | 2 - My conversations
                    | 3 - My identity
                    | 4 - My directory
                    ===============================""");
            while (typing) {
                try {
                    System.out.print("Type your command: ");
                    input = scanner.nextInt();
                    if (commands.containsKey(input) || input == 0) {
                        typing = false;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error! Unrecognized command!");
                    scanner.next();
                }
            }

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
