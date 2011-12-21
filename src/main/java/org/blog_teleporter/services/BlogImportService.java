package org.blog_teleporter.services;

import java.util.List;

import org.blog_teleporter.models.TextPost;

public interface BlogImportService {
    public List<TextPost> getTextPostsByCrawl(String blogUrl, String articleUrlPrefix);
}
