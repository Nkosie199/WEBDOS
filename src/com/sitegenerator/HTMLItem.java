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



    String thumbnail(String thumbnailPath, String name, String href){
        clearBuilder();
        builder.append("\n<div class=\"w3-col l2 m4 s6\">")
                .append("\n<div class=\"w3-card-4\">")
                .append("<a href=\"").append(href).append("\">")
                .append("<img src=")
                .append(thumbnailPath)
                .append(" style=\"width:100%\"")
                .append(">").
                append("<div class=\"w3-container w3-white\">")
                .append("<h4><b>").append(name).append("</b></h4>")
                .append("<p>").append(name).append("</p>")
                .append("</div>")
                .append("</a>")
                .append("</div></div>");

        return builder.toString();
    }

    String materialThumbnail(String thumbnailPath,String name,String href){
        clearBuilder();
        builder.append("\n<div class=\"w3-quarter w3-section\">")
                .append("<div class=\"w3-card-4 w3-center w3-theme-dark\">")
                .append("<a href=\"").append(href).append("\">")
                .append("<h4>").append(name).append("</h4>")
                .append("</a>").append("</div>")
                .append("</div>");

        return builder.toString();
    }



     String navigationBarItem(String href, String name){
        clearBuilder();
        builder.append("\n")
                .append("<a href=\"")
                .append(href)
                .append("\"")
                .append("class=\"w3-bar-item w3-button w3-border-right\">")
                .append(name)
                .append("</a>")
                .append("");
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
