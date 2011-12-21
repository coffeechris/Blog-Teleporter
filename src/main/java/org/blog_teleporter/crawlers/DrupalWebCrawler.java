package org.blog_teleporter.crawlers;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class DrupalWebCrawler extends WebCrawler {
    private Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    public boolean shouldVisit(WebURL webUrl) {
        String url = webUrl.getURL().toLowerCase();
        if (filters.matcher(url).matches()) {
            return false;
        }

        String blogUrl = (String) getConfigs().get("blogUrl");
        if (StringUtils.isNotBlank(blogUrl) && url.startsWith(blogUrl)) {
            return true;
        }
        
        String articleUrlPrefix = (String) getConfigs().get("articleUrlPrefix");
        if (StringUtils.isNotBlank(articleUrlPrefix) && url.startsWith(articleUrlPrefix)) {
            return true;
        }
        
        return false;
    }
    
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();         
        String text = page.getText();
        List<WebURL> links = page.getURLs();
        int parentDocid = page.getWebURL().getParentDocid();
        
        /*
         * Using the crawlerConfigs to check for acceptable urls. Only
         * these urls will be processed.
         */
        String articleUrlPrefix = (String) getConfigs().get("articleUrlPrefix");
        if (StringUtils.isNotBlank(articleUrlPrefix) && url.startsWith(articleUrlPrefix)) {
            System.out.println("Found acceptable URL!");
            System.out.println("Docid: " + docid);
            System.out.println("URL: " + url);
            System.out.println("Text length: " + text.length());
            System.out.println("Number of links: " + links.size());
            System.out.println("Docid of parent page: " + parentDocid);
            System.out.println("=============");                    
        }
    }    
}
