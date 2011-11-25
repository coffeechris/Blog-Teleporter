package org.blog_teleporter.models.form;

public class DeletePosts {
    private String blogName;
    private String tag;
    private boolean removable = false;
    
    public String getBlogName() {
        return blogName;
    }
    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public boolean isRemovable() {
        return removable;
    }
    public void setRemovable(boolean removable) {
        this.removable = removable;
    }
}
