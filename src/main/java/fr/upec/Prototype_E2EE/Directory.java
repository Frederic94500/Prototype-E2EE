package fr.upec.Prototype_E2EE;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Directory {
    final static String path = new File("directory").getAbsolutePath();
    public HashMap<String, byte[]> directory;

    public Directory() throws IOException {
        File f = new File(path);
        if (f.exists()) {
            //FileInputStream file = new FileInputStream(path);
            this.directory = textFileToHashMap();
        } else {
            FileInputStream file = new FileInputStream(path);
        }
    }

    public static HashMap<String, byte[]> textFileToHashMap() throws IOException {
        HashMap<String, byte[]> map = new HashMap<>();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file)); // yves:OUGLIYGjhmiugflggliyfliyful
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] separated = line.split(":");
            String name = separated[0].trim();
            String pubKey = separated[1].trim();
            byte[] bytePubKey = pubKey.getBytes();
            if (!name.equals("") && !pubKey.equals("")) {//"c/mialy
                map.put(name, bytePubKey);
            }
        }
        br.close();
        return map;
    }
    //je ne vois l'interet de save en bas64 ça rajoute juste du travail;
    public static void saveIntoDirectory(String name,byte[] pubkey) throws IOException{

        File f = new File("directory.txt");
        f.createNewFile();
        if (f.exists()) {
            String encodedString = Base64.getEncoder().encodeToString(name.getBytes());
            String encodepubkey=Base64.getEncoder().encodeToString(pubkey);
            FileWriter fw = new FileWriter("directory.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(encodedString);
            bw.write(":");
            bw.write(encodepubkey);
            bw.newLine();
            bw.close();
            System.out.println("écriture dans fichier...");
        } else {

            System.out.println("creation fichier...");
        }
    }

    /**
     * Search in Directory if the otherPubKey is present
     *
     * @param otherPubKey The otherPubKey
     * @return Return a boolean if it is present
     */
    public boolean isInDirectory(byte[] otherPubKey) {
        for (Map.Entry<String, byte[]> entry : directory.entrySet()) {
            if (Arrays.equals(otherPubKey, entry.getValue())) {
                return true;
            }
        }
        return false;
    }
    public static void saveIntoDirectory(){

    }
}
