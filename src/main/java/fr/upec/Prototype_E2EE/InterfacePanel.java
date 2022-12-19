package fr.upec.Prototype_E2EE;

import fr.upec.Prototype_E2EE.MyState.MyState;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

public class InterfacePanel {
    private MyState user;

    public InterfacePanel() throws GeneralSecurityException, IOException {
        this.user = new MyState();
        System.out.println("bienvenue sur le menu");
        System.out.println("0 retour arriere");
        System.out.println("1 Mon Identité");
        System.out.println("2 Directory");
        System.out.println("3 Créer conversation***");
        System.out.println("4 Listes de conversation***");
        Scanner sc = new Scanner(System.in);

        int e = sc.nextInt();
        if (e == 1) {
            MyIdentity();
        } else if (e == 2) {
            manageDirectory();
        } else if (e == 3) {
            createConversation();
        } else if (e == 4) {
            listConversation();
        }

    }

    public void MyIdentity() throws GeneralSecurityException, IOException {

        Scanner sc = new Scanner(System.in);
        System.out.println("0-retour");
        System.out.println("1-afficher pubkey");
        System.out.println("2-remplacer pubkey");
        int e = sc.nextInt();
        if (e == 0) {
            new InterfacePanel();//retour arriere
        } else if (e == 1) {
            System.out.println("-----BEGIN RSA PUBLIC KEY-----");
            PublicKey pubKey = user.getMyKeyPair().getMyPublicKey();
            byte[] pubKey_byte = pubKey.getEncoded();
            String str_key = Base64.getEncoder().encodeToString(pubKey_byte);
            System.out.println(str_key.length());
            System.out.println(str_key);
            System.out.println("-----END RSA PUBLIC KEY-----");
        } else if (e == 2) {
            user.replaceMyKeyPair();
        }


    }

    public void createConversation() {

    }

    public void listConversation() {

    }

    public void manageDirectory() throws GeneralSecurityException, IOException {

        System.out.println("0-retour");
        System.out.println("1-ajouter une personne");
        System.out.println("2-supprimer une personne");
        Scanner sc = new Scanner(System.in);
        int e = sc.nextInt();
        if (e == 0) {
            new InterfacePanel();//retour arriere
        } else if (e == 1) {

            //user.getMyDirectory().addPerson();
            System.out.println("pas encore défini");

        } else if (e == 2) {
            Scanner s = new Scanner(System.in);
            String se = sc.nextLine();
            user.getMyDirectory().deletePerson(se);
        }

    }


}
