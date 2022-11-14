package fr.upec.Prototype_E2EE;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static javax.crypto.KeyGenerator.getInstance;

public class Keys {
    public static KeyPairGenerator generate() throws NoSuchAlgorithmException {
       return KeyPairGenerator.getInstance("EC");
    }
}
