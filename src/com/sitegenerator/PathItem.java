package com.sitegenerator;

import java.nio.file.Path;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
public class PathItem {
    private String name;
    private Path path;

    PathItem(Path path){
        this.path=path;
        name=path.getFileName().toString();
    }

    /**
     * @return the path on which the item can be found
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return the name of the path item not including path information
     */
    public String getName() {
        return name;
    }
}
