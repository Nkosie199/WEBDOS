package com.sitegenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to create html pages from directory objects using a template
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
class PageCreator {

    //Constants for the sections that need to be replaced in the template
    private static final String TITLE="$title";
    private static final String BODY="$body";
    private static final String MENU="$menu";
    private static final String NAVIGATION="$nav";
    private static final String PAGE_HEADING="$pageHeading";
    private static final String STYLE_SHEET="$stylesheet";

    static final String SITE_FOLDER="MyStaticSite";
    static final String THUMBNAIL_FOLDER="thumbs";

    private String siteDirectoryString;
    private Path sitePagesFolderPath;
    private List<Directory> directories;

    private HTMLItem htmlItem;
    private IOManager ioManager;
    private StringBuffer templateBuffer;
    private StringBuffer homeTemplateBuffer;
    private StringBuffer folderTemplateBuffer;

    private PageCreator(String siteDirectory){
        this.siteDirectoryString =siteDirectory;
        directories=new ArrayList<>();
        ioManager=new IOManager();
        createSiteFolder();
        getTemplates();
        writeStyleSheet();
        htmlItem=new HTMLItem();

    }

    PageCreator(String path, List<Directory> directories) {
        this(path);
        this.directories=directories;
        makeHomePage();
        
    }

    /**
     * get template from resources
     */
    private void getTemplates(){
        homeTemplateBuffer =ioManager.getTextResource("template_home.html");
        templateBuffer=ioManager.getTextResource("template_page.html");
        folderTemplateBuffer=ioManager.getTextResource("template_directory.html");
    }
    
    private void writeStyleSheet(){
        Path dest=sitePagesFolderPath.resolve("w3.css");
        ioManager.copyResource("w3.css",dest);
    }
    
    private void makeHomePage(){
        String htmlPageString= homeTemplateBuffer.toString();
        htmlPageString=htmlPageString
                .replace(TITLE,"home")
                .replace(PAGE_HEADING,"Home")
                .replace(STYLE_SHEET,siteDirectoryString+"/"+SITE_FOLDER+"/w3.css");

        htmlPageString=addHomePageContent(htmlPageString);
        writePage(htmlPageString,"index");
    }
    
    private String addHomePageContent(String htmlString){
        StringBuilder builder = new StringBuilder();
        Path sitePath=Paths.get(siteDirectoryString);
        builder.append(htmlItem.materialThumbnail("#",
                "All",
                "All.html"));
        builder.append(htmlItem.materialThumbnail("#",
                "Directories",
                sitePath.getFileName()+".html"));
        builder.append(htmlItem.materialThumbnail("#",
                "File Type<TBD>",
                "#"));

        return htmlString.replace(BODY, builder);
    }

    /**
     * Create html pages corresponding to the directory structure
     */
    void createByDirectories(){
        for(Directory dir:directories){
            this.createPageFromDirectory(dir);
        }
    }

    /**
     *
     * @param directory the directory from which a page will be created
     */
    private void createPageFromDirectory(Directory directory){
        String htmlPageString= folderTemplateBuffer.toString();
        htmlPageString=htmlPageString
                .replace(TITLE,directory.getName())
                .replace(PAGE_HEADING,directory.getName())
                .replace(STYLE_SHEET,siteDirectoryString+"/"+SITE_FOLDER+"/w3.css");

        //htmlPageString=addMenu(directory.getSubDirectories(),htmlPageString);
        htmlPageString=addDirectoryMenu(directory,htmlPageString);
        htmlPageString=addContent(directory.getContentList(),htmlPageString);
        htmlPageString=addNavigationMenu(directory,htmlPageString);
        writePage(htmlPageString,directory.getName());

    }

    /**
     * creates a page listing all the content in the chosen site directory
     * @param contentList list of all content within the site directory
     */
    void createAllContentPage(List<Content> contentList){
        String htmlPageString= templateBuffer.toString();
        htmlPageString=htmlPageString
                .replace(TITLE,"All")
                .replace(PAGE_HEADING,"All")
                .replace(STYLE_SHEET,siteDirectoryString+"/"+SITE_FOLDER+"/w3.css");

        htmlPageString=addMenu(directories,htmlPageString);
        htmlPageString=addContent(contentList,htmlPageString);
        writePage(htmlPageString,"All");
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
            builder.append(htmlItem.navigationBarItem(dir.getName()+".html",dir.getName()));
        }

        return htmlString.replace(MENU,builder.toString());
    }

    /**
     * create a menu from the list of directories. For the current version all directories are
     * treated as if they are on the same navigation level.
     * i.e a directory and its subdirectories are on the same level;
     *
     * @param directory list of directories
     * @param htmlString string containing the html code for the page to be created
     * @return the htmlString with the menu placeholder replaced with the actual menu
     */
    private String addDirectoryMenu(Directory directory, String htmlString){
        StringBuilder builder=new StringBuilder();

        for (Directory dir:directory.getSubDirectories()){
            builder.append(htmlItem.navigationBarItem(dir.getName()+".html",dir.getName()));
        }

        return htmlString.replace(MENU,builder.toString());
    }

    private String addNavigationMenu(Directory directory,String htmlString){
        StringBuilder builder=new StringBuilder();
        ArrayList<Directory> directories=new ArrayList<>();
        directories.add(directory);
        while (directory.hasParent()){
            directory=directory.getParent();
            directories.add(directory);
        }
        Collections.reverse(directories);
        for(Directory dir:directories){
            builder.append(htmlItem.navigationBarItem(dir.getName()+".html",dir.getName()));
        }

        return htmlString.replace(NAVIGATION,builder.toString());
    }


    /**
     * add content to the html page
     * @param contentList a list pf the content to add
     * @return the htmlString with the content placeholder replaced with the actual content
     */
    private String addContent(List<Content> contentList,String htmlString){
        StringBuilder builder=new StringBuilder();
        for(Content content:contentList){
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
     */
    private void writePage(String htmlString,String pageName){
        Path path=sitePagesFolderPath.resolve(pageName+".html");

        ioManager.writeStringToFile(htmlString,path);
    }
    
    private void createSiteFolder(){
        sitePagesFolderPath=ioManager.makeDirectory(SITE_FOLDER,siteDirectoryString);
    }
    

    Path homePagePath(){
        return this.sitePagesFolderPath.resolve("index.html");
    }
    
   


}
