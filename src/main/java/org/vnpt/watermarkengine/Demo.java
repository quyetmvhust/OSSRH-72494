package org.vnpt.watermarkengine;

public class Demo {
    private static final String SECRET_KEY = "super_secret_key_watermark_engine_2021";
    public static void main(String[] args) {
        String originalString = "howtodoinjava.com";

        String encryptedString = AES256.encrypt(originalString, SECRET_KEY);
        String decryptedString = AES256.decrypt(encryptedString, SECRET_KEY);

        System.out.println(originalString);
        System.out.println(encryptedString);
        System.out.println(decryptedString);
    }

}
