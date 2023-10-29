package com.example;

import java.io.File;

public class SupportFunc {

    private static SupportFunc instance;

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
}
