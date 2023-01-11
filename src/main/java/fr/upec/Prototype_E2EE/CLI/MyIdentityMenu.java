package fr.upec.Prototype_E2EE.CLI;

import fr.upec.Prototype_E2EE.MyState.MyState;
import fr.upec.Prototype_E2EE.Tools;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Scanner;

public class MyIdentityMenu implements InterfaceCLI {
    private static void getMyPubKey(MyState myState) {
        System.out.println("Here is your public key:\n");
        System.out.println("-----BEGIN EC PUBLIC KEY-----");
        PublicKey pubKey = myState.getMyKeyPair().getMyPublicKey();
        byte[] pubKeyByte = pubKey.getEncoded();
        String str_key = Tools.toBase64(pubKeyByte);
        System.out.println(str_key);
        System.out.println("-----END EC PUBLIC KEY-----\n");
    }

    @Override
    public void menu(Scanner scanner, MyState myState) throws IOException, GeneralSecurityException {
        boolean cli = true;
        int input;

        do {
            System.out.println("""
                    ========== My Identity Menu ==========
                    | 0 - Return back
                    | 1 - Show my Identity
                    | 2 - Replace my Identity
                    ======================================""");
            input = Tools.getInput(scanner, 2);

            if (input == 0) {
                cli = false;
            } else if (input == 1) {
                getMyPubKey(myState);
            } else if (input == 2) {
                myState.replaceMyKeyPair();
                myState.save();
            }
        } while (cli);
    }
}
