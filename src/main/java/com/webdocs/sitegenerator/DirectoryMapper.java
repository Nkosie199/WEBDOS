package com.webdocs.sitegenerator;

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
 * <p>
 * Class to traverse the directory structure and create Directory Objects
 */
public class DirectoryMapper extends SimpleFileVisitor<Path> {
    private Map<String, Directory> directories;
    private List<Content> contentList;

    public DirectoryMapper() {
        directories = new HashMap<>();
        contentList = new ArrayList<>();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) {
        Directory directory = new Directory(path);
        if (directory.getName().equals(PageCreator.THUMBNAIL_FOLDER)
                || directory.getName().equals(PageCreator.SITE_FOLDER)) {
            return FileVisitResult.CONTINUE;
        }

        String parentPathString = path.getParent().toString();
        if (directories.containsKey(parentPathString)) {
            directories.get(parentPathString).addSubDirectory(directory);
        }
        directories.put(path.toString(), directory);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        Directory containingDirectory = directories.get(path.getParent().toString());
        if (containingDirectory != null) {
            Content content = new Content(path);
            containingDirectory.addContent(content);
            contentList.add(content);
            content.putMetadata("Creation Time", basicFileAttributes.creationTime().toString());
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Directory> getAllDirectories() {
        return new ArrayList<>(directories.values());
    }

    public List<Content> getContentList() {
        return contentList;
    }
}
