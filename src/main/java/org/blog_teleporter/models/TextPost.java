package org.blog_teleporter.models;

public class TextPost {
    private Long id;
    private String title;
    
    public TextPost(Long id, String title) {
        this.id = id;
        this.title = title;
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
}
