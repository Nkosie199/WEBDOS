package com.sitegenerator;

import java.util.Arrays;
import java.util.List;

/**
 * @author brian
 * @version 1.0
 * @since 2018/08/28
 *
 * class to create html strings for items used in the construction of a static site
 */
class HTMLItem {
    private StringBuilder builder=new StringBuilder();

    String thumbnail(String thumbnailPath,String name,String href){
        clearBuilder();
        builder.append("\n<div class=\"thumb\">")
                .append("<a href=\"").append(href).append("\">")
                .append("<img src=")
                .append(thumbnailPath)
                .append(">").append("</a>")
                .append("<p>").append(name)
                .append("</p>")
                .append("</div>");

        return builder.toString();
    }

     String navigationMenuItem(String href,String name){
        clearBuilder();
        builder.append("\n<p>")
                .append("<a href=\"")
                .append(href)
                .append("\">")
                .append(name)
                .append("</a>")
                .append("</p>");
        return builder.toString();
    }

    String paragraph(String... text){
         clearBuilder();
        List<String> lines= Arrays.asList(text);
        for(String line:lines){
            builder.append("<p>")
                    .append(line)
                    .append("</p>");
        }

        return builder.toString();
    }

    private void clearBuilder(){
        builder.delete(0,builder.length());
    }
}
