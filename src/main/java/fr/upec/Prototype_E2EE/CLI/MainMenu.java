package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;

import java.util.Scanner;

public class MainMenu {
    public static void mainMenu(Scanner scanner, MyState myState) {
        boolean cli = true;
        while (cli) {
            System.out.println("""
                    ========== Main Menu ==========
                    | 0 - Quit
                    | 1 - Create a new conversation
                    | 2 - My conversations
                    | 3 - My identity
                    ===============================""");
            System.out.print("Type your command: ");
            switch (scanner.nextInt()) {
                case 0:
                    cli = false;
                    break;
                case 1:
                    break;
                default:
                    System.out.println("Error! Unrecognized command!");
            }
        }
    }
}
