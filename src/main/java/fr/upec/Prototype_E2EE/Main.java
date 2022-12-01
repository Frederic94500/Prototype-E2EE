package fr.upec.Prototype_E2EE;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        //class directory:hashmap(nom,pubkey:string) <---jsonfile
        // ,verifier si dans annuaire il y une clé
        //scéanario
        // users1 ->>>>> users2
        // enregistrer clé public de la personne dans l'annuaire les 2
        // createmessage1(pubkey,salt  ,   heure ) les 2 cotés;
        //prendre en charge le message  :generate  symkey
        //createmessage2 //sign and cipher secretbuild
        // handlemessage2 user2

        // send message


        // MyState1() MyState2()
        InterfacePanel start = new InterfacePanel();
        
    }
}
