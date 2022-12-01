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

        int e = sc.nextInt();
        if (e == 0) {
            new InterfacePanel();
        } else {
            System.out.println("-----BEGIN RSA PUBLIC KEY-----");
            PublicKey pubKey = user.getMyKeyPair().getMyPublicKey();
            byte[] pubKey_byte = pubKey.getEncoded();
            String str_key = Base64.getEncoder().encodeToString(pubKey_byte);
            System.out.println(str_key.length());
            System.out.println(str_key);
            System.out.println("-----END RSA PUBLIC KEY-----");
        }


    }

    public void manageDirectory() {

    }

    public void createConversation() {

    }

    public void listConversation() {

    }


}
