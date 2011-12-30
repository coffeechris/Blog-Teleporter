package org.blog_teleporter.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
        
        return false;
    }
    
    public void visit(Page page) {
        /*
         * Using the crawlerConfigs to check for acceptable urls. Only
         * these urls will be processed.
         */
        String articleUrlPrefix = (String) getConfigs().get("articleUrlPrefix");
        if (StringUtils.isNotBlank(articleUrlPrefix) && page.getWebURL().getURL().startsWith(articleUrlPrefix)) {
            DrupalBlogContentHandler drupalBlogContentHandler = new DrupalBlogContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            try {
                parseContext.set(HtmlMapper.class, AllTagsMapper.class.newInstance());
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HtmlParser parser = new HtmlParser();
            
            String html = page.getHTML();
            InputStream input = IOUtils.toInputStream(html);
            try {
                parser.parse(input, drupalBlogContentHandler, metadata, parseContext);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TikaException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    
            if (drupalBlogContentHandler.isBlogEntry()) {
                System.out.println(drupalBlogContentHandler.getTitle() + " - " + page.getWebURL());
            }
        }
    } 
    
    public static void main (String args[]) throws Exception {
        URL url = new URL("http://chrisjordan.ca/node/33");
        InputStream input = url.openStream();
        
        DrupalBlogContentHandler drupalBlogContentHandler = new DrupalBlogContentHandler();
        
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        parseContext.set(HtmlMapper.class, AllTagsMapper.class.newInstance());
        
        HtmlParser parser = new HtmlParser();
                
        parser.parse(input, drupalBlogContentHandler, metadata, parseContext);
        
        System.out.println("is blog entry:\n" + drupalBlogContentHandler.isBlogEntry());
        System.out.println("title:\n" + drupalBlogContentHandler.getTitle());
        System.out.println("tags:\n" + drupalBlogContentHandler.getTags());
        System.out.println("blog entry:\n" + drupalBlogContentHandler.getBlogEntry());
    }
}

class DrupalBlogContentHandler extends ToHTMLContentHandler {
    
    public String blogEntryStartComment = "##### BLOG_ENTRY_START #####";
    public String blogEntryEndComment = "##### BLOG_ENTRY_END #####";

    private boolean processingDataBlock = false;
    private int dataBlockTagDepth = 0;
    private boolean processingBlogEntry = false;
    private int blogEntryTagDepth = 0;
    private boolean processingTitle = false;
    private boolean processingTaxTerm = false;
    private boolean isBlogEntry = false;
    
    private String blogEntry;
    private StringBuffer title = new StringBuffer();
    private StringBuffer submissionDate = new StringBuffer();
    private StringBuffer currentTag = new StringBuffer();
    private List<String> tags = new ArrayList<String>();
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        //Found the correct block that has all the data that we are interested in
        if (qName.equalsIgnoreCase("div") && StringUtils.equals(atts.getValue("class"), "left-corner") &&
                StringUtils.isBlank(title)) {
            processingDataBlock = true;
        }
        if (processingDataBlock) {
            dataBlockTagDepth ++;
        
            //Inserting at the beginning of the blog entry the start comment
            if (qName.equalsIgnoreCase("div") && StringUtils.equals(atts.getValue("class"), "content clear-block")) {
                characters(blogEntryStartComment.toCharArray(), 0, blogEntryStartComment.length());
                processingBlogEntry = true;
            }
            if (processingBlogEntry) {
                blogEntryTagDepth ++;
            }
            
            //Found the title
            if (qName.equalsIgnoreCase("h2") && StringUtils.isBlank(title)) {
                processingTitle = true;
            }
            
            //Found taxonomy term
            if (qName.equalsIgnoreCase("li") && StringUtils.contains(atts.getValue("class"), "taxonomy_term")) {
                processingTaxTerm = true;
            }
            
            //It is a real blog entry
            if (qName.equalsIgnoreCase("li") && StringUtils.contains(atts.getValue("class"), "blog_usernames_blog")) {
                isBlogEntry = true;
            } 
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        
        if (processingTitle) {
            title.append(ch, start, length);
        }
        
        if (processingTaxTerm) {
            currentTag.append(ch, start, length);
        }
    }  
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {  
        //Title has ended
        if (processingTitle) {
            processingTitle = false;
        }
        
        //Taxonomy term has ended
        if (processingTaxTerm) {
            tags.add(currentTag.toString());
            
            currentTag = new StringBuffer();
            processingTaxTerm = false;
        }
        
        //Inserting at the end of the blog entry the end comment
        if (processingBlogEntry) {
            blogEntryTagDepth --;
            if (blogEntryTagDepth == 0) {
                characters(blogEntryEndComment.toCharArray(), 0, blogEntryEndComment.length());
                processingBlogEntry = false;
            }
        }
        
        //Data block has ended. Stop processing
        if (processingDataBlock) {
            dataBlockTagDepth --;
            if (dataBlockTagDepth == 0) {
                processingDataBlock = false;
            }
        }
        
        super.endElement(uri, localName, qName);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        
        //Extract the blog entry using the start and end comments
        String doc = toString();
        if (doc.contains(blogEntryStartComment) && doc.contains(blogEntryEndComment)) {
            blogEntry = doc.substring(doc.indexOf(blogEntryStartComment) + blogEntryStartComment.length(),
                                         doc.indexOf(blogEntryEndComment));
        }
    }
    
    public String getBlogEntry() {
        return isBlogEntry ? blogEntry : null;
    }

    public String getTitle() {
        return isBlogEntry ? title.toString() : null;
    }

    public String getSubmissionDate() {
        return isBlogEntry ? submissionDate.toString() : null;
    }

    public List<String> getTags() {
        return isBlogEntry ? tags : null;
    }

    public boolean isBlogEntry() {
        return isBlogEntry;
    }

    public String getBlogEntryStartComment() {
        return blogEntryStartComment;
    }

    public void setBlogEntryStartComment(String blogEntryStartComment) {
        this.blogEntryStartComment = blogEntryStartComment;
    }

    public String getBlogEntryEndComment() {
        return blogEntryEndComment;
    }

    public void setBlogEntryEndComment(String blogEntryEndComment) {
        this.blogEntryEndComment = blogEntryEndComment;
    }  
}