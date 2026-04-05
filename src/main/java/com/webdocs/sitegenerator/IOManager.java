package com.webdocs.sitegenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/31
 */
public class IOManager {

    public Path makeDirectory(String directoryName, String parentDirectoryPath) {
        String separator = File.separator;
        File file = new File(parentDirectoryPath + separator + directoryName + separator);
        file.mkdirs();
        return Paths.get(file.getPath());
    }

    public void copyResource(String resourceName, Path destination) {
        try {
            InputStream input = getClass().getResourceAsStream("/sitegenerator/" + resourceName);
            if (input == null) {
                // fallback to same-package resource
                input = getClass().getResourceAsStream(resourceName);
            }
            if (input == null) {
                System.err.println("Resource not found: " + resourceName);
                return;
            }
            PrintStream output = new PrintStream(Files.newOutputStream(destination));
            Scanner scanner = new Scanner(input);
            while (scanner.hasNextLine()) {
                output.println(scanner.nextLine());
            }
            scanner.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public StringBuffer getTextResource(String resourceName) {
        StringBuffer buffer = new StringBuffer();
        try {
            InputStream input = getClass().getResourceAsStream("/sitegenerator/" + resourceName);
            if (input == null) {
                input = getClass().getResourceAsStream(resourceName);
            }
            if (input == null) {
                System.err.println("Template resource not found: " + resourceName);
                return buffer;
            }
            Scanner scanner = new Scanner(input);
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void writeStringToFile(String data, Path filePath) {
        try {
            PrintStream printStream = new PrintStream(Files.newOutputStream(filePath));
            Scanner scanner = new Scanner(data);
            while (scanner.hasNextLine()) {
                printStream.println(scanner.nextLine());
            }
            scanner.close();
            printStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
