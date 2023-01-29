package fr.upec.Prototype_E2EE;

import fr.upec.Prototype_E2EE.CLI.MainMenu;
import fr.upec.Prototype_E2EE.MyState.MyState;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

/**
 * Main class to start the Command Line Interface (CLI)
 */
public class Main {
    /**
     * Main function
     *
     * @param args Arguments
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     * @throws IOException              Throws IOException if there is an I/O exception
     */
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        System.out.println("""
                 _____           _        _                      ______ ___  ______ ______\s
                |  __ \\         | |      | |                    |  ____|__ \\|  ____|  ____|
                | |__) | __ ___ | |_ ___ | |_ _   _ _ __   ___  | |__     ) | |__  | |__  \s
                |  ___/ '__/ _ \\| __/ _ \\| __| | | | '_ \\ / _ \\ |  __|   / /|  __| |  __| \s
                | |   | | | (_) | || (_) | |_| |_| | |_) |  __/ | |____ / /_| |____| |____\s
                |_|   |_|  \\___/ \\__\\___/ \\__|\\__, | .__/ \\___| |______|____|______|______|
                                               __/ | |                                    \s
                                              |___/|_|                                    \s""");
        MyState myState = null;
        SecretKey secretKey;
        String hashedPassword;
        if (Tools.isFileExists(MyState.FILENAME)) {
            do {
                hashedPassword = Tools.getPassword();
                secretKey = Tools.loadSecretKey(hashedPassword);
                try {
                    myState = MyState.load(secretKey, hashedPassword);
                } catch (AEADBadTagException e) {
                    System.out.println("Wrong password! Please retry!");
                }
            } while (myState == null);
        } else {
            hashedPassword = Tools.getConfirmPassword();
            myState = new MyState(hashedPassword);
        }
        new MainMenu().menu(new Scanner(System.in), myState);

        myState.save();
    }
}
