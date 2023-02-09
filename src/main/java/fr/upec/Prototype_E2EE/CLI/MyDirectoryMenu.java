package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Tools;

import java.util.Scanner;

/**
 * My Directory Menu for CLI
 */
public class MyDirectoryMenu implements InterfaceCLI {
    /**
     * Show Directory
     *
     * @param myState User information
     */
    private void showMyDirectory(MyState myState) {
        StringBuilder sb = new StringBuilder();
        if (myState.getMyDirectory().sizeOfDirectory() == 0) {
            sb.append("\nYour directory is empty!\n");
        } else {
            sb.append("\nHere is your directory\n");
            sb.append(myState.getMyDirectory().showDirectory());
        }

        System.out.println(sb);
    }

    /**
     * Add a person with his name and his public key in directory
     *
     * @param scanner Scanner user input
     * @param myState User information
     */
    private void addPerson(Scanner scanner, MyState myState) {
        String name;
        do {
            name = Tools.getInput(scanner, "Name of the person (0 = return back): ");
            if (myState.getMyDirectory().isInDirectory(name)) {
                System.out.println("Already a key with this name!");
            }
        } while (myState.getMyDirectory().isInDirectory(name));
        if (!name.equals("0")) {
            String input;
            String pubKey = "";
            do {
                input = Tools.getInput("Public Key of the person (0 = return back): ");
                if (input.equals("0")) {
                    break;
                } else {
                    try {
                        pubKey = Tools.keyParser(input);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            } while (!Tools.isECPubKey(Tools.toBytes(pubKey)));
            if (!input.equals("0") && !pubKey.equals("")) {
                byte[] pubKeyByte = Tools.toBytes(pubKey);
                myState.getMyDirectory().addPerson(name, pubKeyByte);
                System.out.println("\nThe person has been added!");
            }
        }

        System.out.println();
    }

    /**
     * Delete a person in the directory
     *
     * @param scanner Scanner user input
     * @param myState User information
     */
    private void deletePerson(Scanner scanner, MyState myState) {
        if (myState.getMyDirectory().sizeOfDirectory() == 0) {
            System.out.println("\nNothing to delete!\n");
        } else {
            showMyDirectory(myState);
            String input;
            do {
                input = Tools.getInput(scanner, "Enter the name to delete (0 = return back): ");
                if (input.equals("0")) {
                    break;
                }
            } while (!myState.getMyDirectory().isInDirectory(input));
            if (!input.equals("0")) {
                myState.getMyDirectory().deletePerson(input);
                System.out.println("The person " + input + " has been deleted!");
            }
        }
    }

    /**
     * Menu for Directory
     *
     * @param scanner Scanner user input
     * @param myState User Information
     */
    @Override
    public void menu(Scanner scanner, MyState myState) {
        boolean cli = true;
        int input;

        do {
            System.out.println("""
                    ========== My Directory Menu ==========
                    | 0 - Return back
                    | 1 - Show my Directory
                    | 2 - Add a person
                    | 3 - Delete a person
                    =======================================""");
            input = Tools.getInput(scanner, 3);

            if (input == 0) {
                cli = false;
            } else if (input == 1) {
                showMyDirectory(myState);
            } else if (input == 2) {
                addPerson(scanner, myState);
            } else if (input == 3) {
                deletePerson(scanner, myState);
            }
        } while (cli);
    }
}
