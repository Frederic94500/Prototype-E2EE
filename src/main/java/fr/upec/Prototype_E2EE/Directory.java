package fr.upec.Prototype_E2EE;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Directory {
    final static String path = new File("directory").getAbsolutePath();
    public HashMap<String, byte[]> directory;

    public Directory() throws IOException {
        File f = new File(path);
        if (f.exists()) {
            //FileInputStream file = new FileInputStream(path);
            this.directory = hashMapFromTextFile();
        } else {
            FileInputStream file = new FileInputStream(path);
        }
    }

    public static HashMap<String, byte[]> hashMapFromTextFile() throws IOException {
        HashMap<String, byte[]> map = new HashMap<>();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] separated = line.split(":");
            String name = separated[0].trim();
            String pubKey = separated[1].trim();
            byte[] bytePubKey = pubKey.getBytes();
            if (!name.equals("") && !pubKey.equals("")) {
                map.put(name, bytePubKey);
            }
        }
        br.close();
        return map;
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
}
