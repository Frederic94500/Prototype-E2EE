package fr.upec.Prototype_E2EE;

import java.io.*;
import java.util.HashMap;

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
}
