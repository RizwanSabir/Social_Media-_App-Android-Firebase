package com.example.dosribaar;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MemoryData {


    public static void saveName(String data, Context context) {
        try {
            File path = context.getFilesDir();
            File file = new File(path, "name.txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getName(Context context) {

        String data = "";
        try {
            FileInputStream fis = context.openFileInput("name.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }

    public static void deleteName(Context context) {

        File path = context.getFilesDir();
        File file = new File(path, "name.txt");
        file.delete();

    }

}