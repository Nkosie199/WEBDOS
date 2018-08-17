package com.sitegenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        FileTreeWalker fileTreeWalker=new FileTreeWalker();
        PageCreator creator=new PageCreator(args[0]);
        try {
            //traverse the file tree starting with the user input directory
            //and create directory objects corresponding to the actual directories
            Files.walkFileTree(Paths.get(args[0]),fileTreeWalker);

            //get all the directories and create pages from them
            List<Directory> directories= new ArrayList<>(fileTreeWalker.directories.values());

            //create a page from each directory object
            for(int i=0;i<directories.size();i++){
                creator.createPageFromDirectory(i,directories);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
