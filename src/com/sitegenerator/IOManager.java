package com.sitegenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/31
 */
public class IOManager {

    Path makeDirectory(String directoryName, String parentDirectoryPath){
        String separator= File.separator;
        File file=new File(parentDirectoryPath+separator+directoryName
                +separator);
        file.mkdirs();
        return Paths.get(file.getPath());
    }

    void copyResource(String resourceName,Path destination){
        try {
            InputStream input=getClass().getResourceAsStream(resourceName);
            PrintStream output=new PrintStream(Files.newOutputStream(destination));
            Scanner scanner=new Scanner(input);
            while(scanner.hasNextLine()){
                output.println(scanner.nextLine());
            }
            scanner.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    StringBuffer getTextResource(String resourceName){
        //TODO use delimeter for scanner
        StringBuffer buffer=new StringBuffer();
        //Paths.get(System.getProperty("user.dir")).toString();
        try {
            Scanner scanner=new Scanner(getClass().getResourceAsStream(resourceName));
            while (scanner.hasNextLine()){
                buffer.append(scanner.nextLine()).append("\n");

            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    void writeStringToFile(String data,Path filePath){

        try {
            PrintStream printStream=new PrintStream(Files.newOutputStream(filePath));

            Scanner scanner=new Scanner(data);
            while (scanner.hasNextLine()){
                printStream.println(scanner.nextLine());
            }
            scanner.close();
            printStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
