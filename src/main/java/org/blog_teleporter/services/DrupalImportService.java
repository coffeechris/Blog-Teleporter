package org.blog_teleporter.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blog_teleporter.crawlers.DrupalWebCrawler;
import org.blog_teleporter.models.TextPost;

import edu.uci.ics.crawler4j.crawler.ConfigurableCrawlController;

public class DrupalImportService implements BlogImportService{
    
    private String rootFolder;
    
    private final Log logger = LogFactory.getLog(getClass());

    public List<TextPost> getTextPostsByCrawl(String blogUrl, String articleUrlPrefix, String blogEntryStartComment, String blogEntryEndComment) {
        URL u = null;
        try {
            u = new URL(blogUrl);
        } 
        catch (MalformedURLException e) {
            logger.error("Drupal blog url ("+ blogUrl +") poorily formed", e);
            throw new RuntimeException("Drupal blog url ("+ blogUrl +") poorily formed", e);
        }
        
        String storageDir = rootFolder + u.getHost() + u.getPath().replaceAll("/", "_");
        logger.debug("storage directory: " + storageDir);
        
        //begin blog crawl
        try {
            ConfigurableCrawlController controller = new ConfigurableCrawlController(storageDir);
            
            HashMap<String,Object> crawlerConfigs = new HashMap<String,Object>();
            crawlerConfigs.put("blogUrl", blogUrl);
            crawlerConfigs.put("articleUrlPrefix", articleUrlPrefix);
            crawlerConfigs.put("blogEntryStartComment", blogEntryStartComment);
            crawlerConfigs.put("blogEntryEndComment", blogEntryEndComment);
            controller.setCrawlerConfigs(crawlerConfigs);
            
            controller.addSeed(blogUrl);
            controller.start(DrupalWebCrawler.class, 1);
            
            List<TextPost> posts = new ArrayList<TextPost>();
            List<Object> crawlersData = controller.getCrawlersLocalData();
            for (Object data : crawlersData) {
                @SuppressWarnings("unchecked")
                List<TextPost> crawlerPosts = (List<TextPost>) data;
                posts.addAll(crawlerPosts);
            }
            
            return posts;
        } 
        catch (Exception e) {
            logger.error("Error initializing crawlers", e);
            throw new RuntimeException("Error initializing crawlers", e);
        }
    }
    
    /**
     * Driving method used for development purposes.
     * 
     * @param args 0 - root folder, 1 - blog url, 2 - article url prefix
     */
    public static void main (String args[]) {
        DrupalImportService drupal = new DrupalImportService();
        drupal.setRootFolder(args[0]);
        List<TextPost> posts = drupal.getTextPostsByCrawl(args[1], args[2], null, null);
        
        System.out.println("crawl complete");
        System.out.println("number of posts: " + posts.size());
        for (TextPost post : posts) {
            System.out.println(post);
        }
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }
}
