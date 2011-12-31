package org.blog_teleporter.services;

import java.util.ArrayList;
import java.util.List;

import org.blog_teleporter.models.TextPost;

public class MockBlogImportService implements BlogImportService {

    @Override
    public List<TextPost> getTextPostsByCrawl(String blogUrl, String articleUrlPrefix, String blogEntryStartComment, String blogEntryEndComment) {
        List<TextPost> posts = new ArrayList<TextPost>();
        
        posts.add(new TextPost(null, "test 1", "test body 1", ""));
        posts.add(new TextPost(null, "test 2", "test body 2", ""));
        posts.add(new TextPost(null, "test 3", "test body 3", ""));
        
        return posts;
    }

}
