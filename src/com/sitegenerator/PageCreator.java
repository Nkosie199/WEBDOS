package com.sitegenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to create html pages from directory objects using a template
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
public class PageCreator {

    //Constants for the sections that need to be replaced in the template
    private static final String TITLE="$title";
    private static final String BODY="$body";
    private static final String MENU="$menu";
    private static final String PAGE_HEADING="$pageHeading";
    private static final String STYLE_SHEET="$stylesheet";

    public static final String SITE_FOLDER="MyStaticSite";
    public static final String THUMBNAIL_FOLDER="thumbs";

    private String siteDirectoryString;
    private Path sitePagesFolderPath;
    private String workingDirectory;
    private StringBuffer templateBuffer;

    PageCreator(String siteDirectory){
        this.siteDirectoryString =siteDirectory;
        this.createSiteFolder();
        getTemplate();
        this.writeStyleSheet();
        
    }

    /**
     * get template from working directory and store in a string buffer.
     */
    private void getTemplate(){
        templateBuffer=new StringBuffer();

        Path path= Paths.get("template.html");
        workingDirectory=Paths.get(System.getProperty("user.dir")).toString();//path.getParent().toString();

        try {
            Scanner scanner=new Scanner(getClass().getResourceAsStream("template.html"));
            while (scanner.hasNextLine()){
                templateBuffer.append(scanner.nextLine()).append("\n");
              
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void writeStyleSheet(){
        Path src=Paths.get(this.getClass().getResource("styleTemplate.css").getPath());
        Path dest=this.sitePagesFolderPath.resolve("styleTemplate.css");
        
        try {
            InputStream input=getClass().getResourceAsStream("styleTemplate.css");
            PrintStream output=new PrintStream(Files.newOutputStream(dest));
            Scanner scanner=new Scanner(input);
            while(scanner.hasNextLine()){
                output.println(scanner.nextLine());
            }
            scanner.close();
            output.close();
            //Files.copy(src, dest,StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(PageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param directoryIndex the position at which the directory can be found in the list of directories
     * @param directories the list of directories used primarily to generate a menu for navigation
     */
    void createPageFromDirectory(int directoryIndex,List<Directory> directories){
        Directory directory=directories.get(directoryIndex);

        String htmlPageString=templateBuffer.toString();
        htmlPageString=htmlPageString
                .replace(TITLE,directory.getName())
                .replace(PAGE_HEADING,directory.getName())
                .replace(STYLE_SHEET,siteDirectoryString+"/"+SITE_FOLDER+"/styleTemplate.css");

        htmlPageString=addMenu(directories,htmlPageString);
        htmlPageString=addContent(directory,htmlPageString);

        writePage(htmlPageString,directory);

    }

    /**
     * create a menu from the list of directories. For the current version all directories are
     * treated as if they are on the same navigation level.
     * i.e a directory and its subdirectories are on the same level;
     *
     * @param directories list of directories
     * @param htmlString string containing the html code for the page to be created
     * @return the htmlString with the menu placeholder replaced with the actual menu
     */
    private String addMenu(List<Directory> directories, String htmlString){
        StringBuilder builder=new StringBuilder();
        for (Directory dir:directories){
            builder.append("\n<p>")
                    .append("<a href=\"")
                    .append(dir.getName()).append(".html\"")
                    .append(">")
                    .append(dir.getName())
                    .append("</a>")
                    .append("</p>");
        }

        return htmlString.replace(MENU,builder.toString());
    }

    /**
     * add content to the html page
     * @param directory the directory from which the page is being created
     * @param htmlString htmlString string representing the html code
     * @return the htmlString with the content placeholder replaced with the actual content
     */
    private String addContent(Directory directory,String htmlString){
        StringBuilder builder=new StringBuilder();
        for(Content content:directory.getContentList()){
            builder.append("\n<div class=\"thumb\">")
                    .append("<a href=\"").append(content.getPath().toString()).append("\">")
                    .append("<img src=").append(siteDirectoryString).append("/thumbs/")
                    .append(thumbnailName(content.getName()))
                    .append(">").append("</a>")
                    .append("<p>").append(content.getName())
                    .append("</p>")
                    .append("</div>");
        }
        return htmlString.replace(BODY,builder.toString());
    }

    private String thumbnailName(String contentName){
        int extensionStart=contentName.lastIndexOf(".");
        String extension=contentName.substring(extensionStart);
        return contentName.replace(extension,".jpg");
    }

    /**
     * save the html page.
     * @param htmlString string representing the html code
     * @param directory the directory from which the page is being created
     */
    private void writePage(String htmlString,Directory directory){

        try {

            String separator= File.separator;
            File file=new File(siteDirectoryString+separator+SITE_FOLDER
                    +separator);
            file.mkdirs();
            Path path=Paths.get(file.getPath(),directory.getName()+".html");
            PrintStream printStream=new PrintStream(Files.newOutputStream(path));

            Scanner scanner=new Scanner(htmlString);
            while (scanner.hasNextLine()){
                printStream.println(scanner.nextLine());
            }
            scanner.close();
            printStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createSiteFolder(){
            String separator= File.separator;
            File file=new File(siteDirectoryString+separator+SITE_FOLDER
                    +separator);
            file.mkdirs();
            this.sitePagesFolderPath=Paths.get(file.getPath());
    }


}
