package org.blog_teleporter.services;

import java.util.ArrayList;
import java.util.List;

import org.blog_teleporter.models.TextPost;

public class DrupalImportService implements BlogImportService{
    public List<TextPost> getTextPostsByCrawl(String url) {
        List<TextPost> posts = new ArrayList<TextPost>();
        
        return posts;
    }
}
