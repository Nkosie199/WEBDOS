package com.sitegenerator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/15
 */
class Content extends PathItem {
    private Map<String,String> metadata;


    Content(Path path){
        super(path);
        metadata=new HashMap<>();
    }

    void putMetadata(String dataName, String value){
      metadata.put(dataName,value);
    }
}
