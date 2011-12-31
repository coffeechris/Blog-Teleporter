package org.blog_teleporter.models;

import java.util.List;

public class TextPost {
    private Long id;
    private String title;
    private String body;
    private String date;
    private List<String> tags;
    
    public TextPost(Long id, String title, String body, String date, List<String> tags) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        this.tags = tags;
    }

    public TextPost(Long id, String title, String body, String date) {
        this(id, title, body, date, null);

    }
    
    public TextPost(Long id, String title, String body) {
        this(id, title, body, null, null);
    }

    public TextPost(Long id, String title) {
        this(id, title, null, null, null);
    }

    @Override
    public String toString() {
        return "TumblrTextPost [id=" + id + ", title=" + title + "]";
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
