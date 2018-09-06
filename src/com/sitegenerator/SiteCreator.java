package com.sitegenerator;


import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/28
 */
public class SiteCreator {
    private String startingDirectory;

    public static void main(String[] args) {
        SiteCreator siteCreator=new SiteCreator(args[0]);
        siteCreator.run();

    }

    private SiteCreator(String startingDirectory){
        this.startingDirectory =startingDirectory;
    }

    private void run(){
        DirectoryMapper directoryMapper =new DirectoryMapper();

        try {
            //traverse the file tree starting with the user input directory
            //and create directory objects corresponding to the actual directories
            Files.walkFileTree(Paths.get(startingDirectory), directoryMapper);

            //get all the directories and create pages from them
            PageCreator creator=new PageCreator(startingDirectory, directoryMapper.getAllDirectories());

            //create a page from each directory object
            creator.createByDirectories();
            creator.createAllContentPage(directoryMapper.getContentList());

            //For testing purposes only!!
            Desktop.getDesktop().browse(creator.homePagePath().toUri());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
