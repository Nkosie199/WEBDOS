package com.webdocs.sitegenerator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
public class Content extends PathItem {
    private Map<String, String> metadata;

    Content(Path path) {
        super(path);
        metadata = new HashMap<>();
    }

    void putMetadata(String dataName, String value) {
        metadata.put(dataName, value);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
