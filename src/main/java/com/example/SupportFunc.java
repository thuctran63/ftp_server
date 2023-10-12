package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SupportFunc {

    private static final String AES_ALGORITHM = "AES";
    private static SupportFunc instance;
    Key key = new SecretKeySpec("00112233445566778899AABBCCDDEEFF".getBytes(), "AES");

    public static SupportFunc getInstance() {
        if (instance == null) {
            instance = new SupportFunc();
        }
        return instance;
    }

    public void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public boolean vertify_user(String username, String password) {

        try {
            String filePath = System.getProperty("user.dir") + "\\" + "src\\main\\java\\security\\user.txt";
            System.out.println(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            Map<String, String> usernamePasswordMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                // Tách dòng thành username và password
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    usernamePasswordMap.put(parts[0], parts[1]);
                }
            }
            reader.close();

            if (usernamePasswordMap.containsKey(username) && usernamePasswordMap.get(username).equals(password)) {
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }

    }

    public String decryptData(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);
    }
}
