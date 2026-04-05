package com.webdocs.sitegenerator;

import java.nio.file.Path;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
public class PathItem {
    private String name;
    private Path path;

    PathItem(Path path) {
        this.path = path;
        name = path.getFileName().toString();
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
