package fr.upec.Prototype_E2EE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Tools {
    public static String toBase64(byte[] in) {
        return Base64.getEncoder().encodeToString(in);
    }

    public static String toBase64(String in) {
        return Base64.getEncoder().encodeToString(in.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] toBytes(String in) {
        return Base64.getDecoder().decode(in);
    }
}
