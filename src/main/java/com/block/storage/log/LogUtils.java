package com.block.storage.log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class LogUtils {

    static {
        try {
            Files.createDirectories(Paths.get("./logs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToStorageLog(String string) {

        try {
            final Path path = Paths.get("./logs/storage_log.txt");
            Files.write(path, Arrays.asList(string), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String findFileInfo(String fileName) {

        try (BufferedReader br = new BufferedReader(new FileReader("./logs/storage_log.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(fileName)) {
                    return line;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
