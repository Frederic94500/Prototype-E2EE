package fr.upec.Prototype_E2EE;

import fr.upec.Prototype_E2EE.CLI.MainMenu;
import fr.upec.Prototype_E2EE.MyState.MyState;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        MyState myState = MyState.load();
        new MainMenu().menu(new Scanner(System.in), myState);

        myState.save();
    }
}
