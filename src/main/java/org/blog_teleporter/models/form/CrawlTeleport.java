package org.blog_teleporter.models.form;

public class CrawlTeleport {
    private String blogName;
    private String tag;
    private String targetUrl;
    private boolean preview = false;
    
    @Override
    public String toString() {
        return "CrawlTeleport [blogName=" + blogName + ", tag=" + tag
                + ", targetUrl=" + targetUrl + ", preview=" + preview + "]";
    }
    
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
    public String getTargetUrl() {
        return targetUrl;
    }
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public boolean isPreview() {
        return preview;
    }
    public void setPreview(boolean preview) {
        this.preview = preview;
    }
}
