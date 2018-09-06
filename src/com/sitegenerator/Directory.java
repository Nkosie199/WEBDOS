package com.sitegenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
public class Directory extends PathItem {
    private List<Content> contentList;
    private List<Directory> subDirectories;
    private Directory parent;

    public Directory(Path path){
        super(path);
        contentList=new ArrayList<>();
        subDirectories=new ArrayList<>();
    }

    public void addContent(Content content){
        contentList.add(content);
    }

    public void addSubDirectory(Directory directory){
        directory.parent=this;
        subDirectories.add(directory);

    }

    public List<Directory> getSubDirectories() {
        return subDirectories;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public Directory getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent!=null;
    }
}
