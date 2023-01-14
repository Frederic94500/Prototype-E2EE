package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

/**
 * Interface for CLI
 */
@FunctionalInterface
public interface InterfaceCLI {
    /**
     * Interface function for menu
     *
     * @param scanner User input
     * @param myState User information
     * @throws IOException              Throws IOException if there is an I/O exception
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException;
}
