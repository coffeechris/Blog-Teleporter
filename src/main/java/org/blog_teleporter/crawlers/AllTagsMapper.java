package org.blog_teleporter.crawlers;

import org.apache.tika.parser.html.HtmlMapper;

/**
 * An HtmlMapper that accepts all tags and tributes. I am going on the assumption that if the html is already in a blog, then
 * it has to be some what acceptable. Besides, I am just feeding the tumble beasts.
 */
public class AllTagsMapper implements HtmlMapper {

    @Override
    public String mapSafeElement(String name) {
        return name.toLowerCase();
    }

    @Override
    public boolean isDiscardElement(String name) {
        return false;
    }

    @Override
    public String mapSafeAttribute(String elementName, String attributeName) {
        return attributeName.toLowerCase();
    }
    
}
