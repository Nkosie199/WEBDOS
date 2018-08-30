package com.sitegenerator;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/14
 *
 * Class to traverse the directory structure and create Directory Objects
 */

public class DirectoryMapper extends SimpleFileVisitor<Path> {
    Map<String,Directory> directories;

    DirectoryMapper(){
        directories=new HashMap<>();
    }

    /**
     * Creates a new directory object for the directory about to be visited.
     * If the directory has a parent, add the directory as a subdirectory of the parent.
     * finding subdirectories only works as long as traversal is depth first.
     * the site folder and thumbnail folder(which should be called 'thumbs') are ignored.
     * (thumbnails created using ImageMagick command:
     *  mogrify  -format [desired thumbnail type e.g jpg] -path thumbs -thumbnail [w*h] *.[extension])
     * @param path -
     * @param basicFileAttributes -
     * @return .
     */
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) {

       Directory directory=new Directory(path);
       if(directory.getName().equals(PageCreator.THUMBNAIL_FOLDER)
               || directory.getName().equals(PageCreator.SITE_FOLDER)){

           return FileVisitResult.CONTINUE;
       }

       //add subdirectories if there are any
       String parentPathString=path.getParent().toString();
        if(directories.containsKey(parentPathString)){
            directories.get(parentPathString).addSubDirectory(directory);
        }
        //add directory to Map
        directories.put(path.toString(),new Directory(path));
        return FileVisitResult.CONTINUE;
    }

    /**
     * get the directory in which the file can be found  and add the file to the directory object
     * @param path .
     * @param basicFileAttributes .
     * @return .
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)  {
        Directory containingDirectory=directories.get(path.getParent().toString());
        if(containingDirectory!=null)containingDirectory.addContent(new Content(path));
        return FileVisitResult.CONTINUE;
    }

    public List<Directory> getAllDirectories(){
        return new ArrayList<>(directories.values());
    }


}
