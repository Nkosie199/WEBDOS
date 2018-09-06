package com.sitegenerator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser=new JFileChooser();
       
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showDialog(null,"Select Directory");
        File file=null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            run(file.getAbsolutePath());
          
        }


    }
    
    public static void run(String path){
        FileTreeWalker fileTreeWalker=new FileTreeWalker();
       
        try {
            //traverse the file tree starting with the user input directory
            //and create directory objects corresponding to the actual directories
            Files.walkFileTree(Paths.get(path),fileTreeWalker);

            //get all the directories and create pages from them
            List<Directory> directories= new ArrayList<>(fileTreeWalker.directories.values());
             PageCreator creator=new PageCreator(path,directories);

            //create a page from each directory object
            //creator.createPages();
            Desktop.getDesktop().browse(creator.homePagePath().toUri());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
