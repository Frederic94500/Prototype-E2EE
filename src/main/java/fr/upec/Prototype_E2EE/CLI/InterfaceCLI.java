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
    void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException;
}
