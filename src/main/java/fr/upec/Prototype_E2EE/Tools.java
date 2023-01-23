package fr.upec.Prototype_E2EE;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.util.Arrays.copyOfRange;

/**
 * Frequently used functions
 */
public class Tools {
    /**
     * Get the current time as UNIX Timestamp
     *
     * @return Return UNIX Timestamp
     */
    public static Long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Encode bytes to String Base64
     *
     * @param in Bytes
     * @return Return String as Base64
     */
    public static String toBase64(byte[] in) {
        return Base64.getEncoder().encodeToString(in);
    }

    /**
     * Decode String Base64 to bytes
     *
     * @param in String Base64
     * @return Return Bytes
     */
    public static byte[] toBytes(String in) {
        return Base64.getDecoder().decode(in);
    }

    /**
     * Decode Bytes to PublicKey
     *
     * @param bytesPubKey Bytes Public Key
     * @return Return PublicKey
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static PublicKey toPublicKey(byte[] bytesPubKey) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bytesPubKey));
    }

    /**
     * Decode Bytes to PrivateKey
     *
     * @param privateKeyBytes Bytes Private Key
     * @return Return PrivateKey
     * @throws GeneralSecurityException Throws GeneralSecurityException if there is a security-related exception
     */
    public static PrivateKey toPrivateKey(byte[] privateKeyBytes) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    /**
     * Decode Bytes to SecretKey
     *
     * @param secretKeyBytes SecretKey in byte[]
     * @return Return a SecretKey
     */
    public static SecretKey toSecretKey(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    /**
     * Generate a SecureRandom using AES(256)
     *
     * @return Return a SecureRandom
     * @throws NoSuchAlgorithmException Throws NoSuchAlgorithmException if there is not the expected algorithm
     */
    public static SecureRandom generateSecureRandom() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return new SecureRandom(keyGenerator.generateKey().getEncoded());
    }

    /**
     * Transform Bytes to Long from byte[]
     *
     * @param tab  byte[] source
     * @param from Start index
     * @param to   End index
     * @return Return a Long
     */
    public static Long toLong(byte[] tab, int from, int to) {
        ByteBuffer bb = ByteBuffer.wrap(copyOfRange(tab, from, to));
        return bb.getLong();
    }

    /**
     * Transform Bytes to int from byte[]
     *
     * @param tab  byte[] source
     * @param from Start index
     * @param to   End index
     * @return Return an int
     */
    public static int toInteger(byte[] tab, int from, int to) {
        ByteBuffer bb = ByteBuffer.wrap(copyOfRange(tab, from, to));
        return bb.getInt();
    }

    /**
     * Check if a file exists
     *
     * @param filename File to check
     * @return Return a boolean if the file exists
     */
    public static boolean isFileExists(String filename) {
        return new File(filename).exists();
    }

    /**
     * Create a file
     *
     * @param filename File to create
     */
    public static void createFile(String filename) {
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a file
     *
     * @param filename File to be deleted
     */
    public static void deleteFile(String filename) {
        new File(filename).delete();
    }

    /**
     * Compute the checksum of a file
     *
     * @param filename File to get the digest
     * @return Return an SHA-512 Checksum
     * @throws IOException              Throws IOException if there is an I/O exception
     * @throws NoSuchAlgorithmException Throws NoSuchAlgorithmException if there is not the expected algorithm
     */
    public static String digest(String filename) throws IOException, NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-512").digest(Files.readAllBytes(Path.of(filename)));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Get user input for option in CLI
     *
     * @param scanner Scanner for user input
     * @param max     Maximum number for option
     * @return Return the number of the option
     */
    public static int getInput(Scanner scanner, int max) {
        boolean typing = true;
        int input = 0;

        while (typing) {
            try {
                System.out.print("Type your command: ");
                input = scanner.nextInt();
                if (0 <= input && input <= max) {
                    typing = false;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error! Unrecognized command!");
                scanner.next();
            }
        }

        return input;
    }

    /**
     * Get user input
     *
     * @param scanner  Scanner for user input
     * @param sentence Sentence for the input
     * @return Return user input
     */
    public static String getInput(Scanner scanner, String sentence) {
        System.out.print(sentence);

        String input = scanner.next();
        if (input.equals("0")) {
            return "0";
        }
        return input;
    }

    /**
     * Get user input using nextLine() method
     *
     * @param sentence Sentence for the input
     * @return Return user input
     */
    public static String getInput(String sentence) {
        System.out.print(sentence);
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        if (input.equals("0")) {
            return "0";
        }
        return input;
    }

    /**
     * Verify if the Public Key is an EC key
     *
     * @param pubKey EC Public Key
     * @return Return if is EC Key
     */
    public static boolean isECPubKey(byte[] pubKey) {
        try {
            toPublicKey(pubKey);
        } catch (GeneralSecurityException e) {
            if (new String(pubKey).equals("0")) {
                return false;
            }
            System.out.println("Not a Public Key!");
            return false;
        }
        return true;
    }

    /**
     * Parser for the Public Key from PEM format
     *
     * @param keyPem Public Key
     * @return Return parsed Public Key
     */
    public static String keyParser(String keyPem) {
        if (keyPem.contains("----BEGIN EC PUBLIC KEY-----")) {
            String[] tokens = keyPem.split("-----");
            for (String token : tokens) {
                try {
                    if (Tools.toBytes(token).length == 91) {
                        return token;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else if (Tools.toBytes(keyPem).length == 91) {
            return keyPem;
        }
        throw new IllegalArgumentException("Can't find public key!");
    }
}
