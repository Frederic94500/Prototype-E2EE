package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;

import java.util.Scanner;

@FunctionalInterface
public interface InterfaceCLI {
    void menu(Scanner scanner, MyState myState);
}
