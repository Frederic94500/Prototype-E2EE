package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Scanner;

/**
 * My Identity Menu for CLI
 */
public class MyIdentityMenu implements InterfaceCLI {
    /**
     * Show my Public Key
     *
     * @param publicKey My Public Key
     */
    private void showMyPubKey(PublicKey publicKey) {
        System.out.println("Here is your public key:\n");
        System.out.print("-----BEGIN PUBLIC KEY-----");
        byte[] pubKeyByte = publicKey.getEncoded();
        String publicKeyBase64 = Tools.toBase64(pubKeyByte);
        System.out.print(publicKeyBase64);
        System.out.println("-----END PUBLIC KEY-----\n");
    }

    /**
     * Replace Identity Menu
     *
     * @param scanner User input
     * @param myState User information
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     * @throws IOException              Throws IOException if there is an I/O exception
     */
    private void replaceMyIdentityMenu(Scanner scanner, MyState myState) throws GeneralSecurityException, IOException {
        String input = Tools.getInput(scanner, "Are your sure to replace your identity? (y = yes, other key = no) ");
        if (input.equalsIgnoreCase("y")) {
            myState.replaceMyKeyPair();
            System.out.println("Your identity has been replaced!");
        } else {
            System.out.println("Your identity has not been replaced");
        }
        System.out.println();
    }

    /**
     * Menu for Identity
     *
     * @param scanner Scanner user input
     * @param myState User information
     * @throws IOException              Throws IOException if there is an I/O exception
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    @Override
    public void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException {
        boolean cli = true;
        int input;

        do {
            System.out.println("""
                    ========== My Identity Menu ==========
                    | 0 - Return back
                    | 1 - Show my Identity
                    | 2 - Export Private key
                    | 3 - Replace my Identity
                    ======================================""");
            input = Tools.getInput(scanner, 3);

            if (input == 0) {
                cli = false;
            } else if (input == 1) {
                showMyPubKey(myState.getMyPublicKey());
            } else if (input == 2) {
                System.out.println(myState.exportToJSON());
            } else if (input == 3) {
                replaceMyIdentityMenu(scanner, myState);
            }
        } while (cli);
    }
}
