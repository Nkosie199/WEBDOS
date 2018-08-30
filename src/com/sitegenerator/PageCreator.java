package com.sitegenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    static final String SITE_FOLDER="MyStaticSite";
    static final String THUMBNAIL_FOLDER="thumbs";

    private String siteDirectoryString;
    private Path sitePagesFolderPath;
    private String workingDirectory;
    private StringBuffer templateBuffer;
    private List<Directory> directories;

    private HTMLItem htmlItem;

    private PageCreator(String siteDirectory){
        this.siteDirectoryString =siteDirectory;
        directories=new ArrayList<>();
        this.createSiteFolder();
        getTemplate();
        this.writeStyleSheet();
        htmlItem=new HTMLItem();
    }

    PageCreator(String path, List<Directory> directories) {
        this(path);
        this.directories=directories;
        makeHomePage();
        
    }

    /**
     * get template from working directory and store in a string buffer.
     */
    private void getTemplate(){
        templateBuffer=new StringBuffer();

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
        } catch (IOException ex) {
            Logger.getLogger(PageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void makeHomePage(){
        String htmlPageString=templateBuffer.toString();
        htmlPageString=htmlPageString
                .replace(TITLE,"home")
                .replace(PAGE_HEADING,"Home")
                .replace(STYLE_SHEET,siteDirectoryString+"/"+SITE_FOLDER+"/styleTemplate.css");

        htmlPageString=addMenu(directories,htmlPageString);
        htmlPageString=addHomePageContent(htmlPageString);
        writePage(htmlPageString,null);
    }
    
    private String addHomePageContent(String htmlString){
        String builder = htmlItem.paragraph("This is your static site home page.",
                " Select an item on the menu to navigate to a page");

        return htmlString.replace(BODY, builder);
    }
    
     void createPages(){
        for(Directory dir:directories){
            this.createPageFromDirectory(dir);
        }
    }
    

    /**
     *
     * @param directory the directory from which a page will be created
     */
    private void createPageFromDirectory(Directory directory){
       
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
            builder.append(htmlItem.navigationMenuItem(dir.getName()+".html",dir.getName()));
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
            String thumbString=String.format("%s/%s/%s",this.siteDirectoryString,
                    THUMBNAIL_FOLDER,thumbnailName(content.getName()));
            Path thumbPath=Paths.get(thumbString);
            if(!Files.exists(thumbPath)){
                thumbString=thumbString.replace(thumbnailName(content.getName()),"default.jpg");
            }
            builder.append(htmlItem.thumbnail(thumbString,
                    content.getName(),
                    content.getPath().toString()));

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
             Path path=Paths.get(file.getPath());
            if(directory!=null){
                path=path.resolve(directory.getName()+".html");
            }
            else {
                path=path.resolve("index.html");
            }
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
    
    public Path siteFolderPath(){
        return this.sitePagesFolderPath;
    }
    
    Path homePagePath(){
        return this.sitePagesFolderPath.resolve("index.html");
    }
    
   


}
