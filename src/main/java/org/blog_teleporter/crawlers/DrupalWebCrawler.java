package org.blog_teleporter.crawlers;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.blog_teleporter.models.TextPost;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class DrupalWebCrawler extends WebCrawler {
    private Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private String blogEntryStartComment;
    private String blogEntryEndComment;
    
    private List<TextPost> posts;
    
    private final Log logger = LogFactory.getLog(getClass());

    public DrupalWebCrawler () {
        super();
        
        blogEntryStartComment = "##### BLOG_ENTRY_START #####";
        blogEntryEndComment = "##### BLOG_ENTRY_END #####";
        
        posts = new ArrayList<TextPost>();
    }
    
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
            if (StringUtils.isNotBlank((String) getConfigs().get("blogEntryStartComment"))) {
                blogEntryStartComment = (String) getConfigs().get("blogEntryStartComment");
            }
            if (StringUtils.isNotBlank((String) getConfigs().get("blogEntryEndComment"))) {
                blogEntryEndComment = (String) getConfigs().get("blogEntryEndComment");
            }
            
            if (StringUtils.isBlank(blogEntryStartComment) || StringUtils.isBlank(blogEntryEndComment)) {
                logger.error("the blog entry comments must not be blank");
                throw new RuntimeException("the blog entry comments must not be blank");
            }
            
            DrupalBlogContentHandler drupalBlogContentHandler = new DrupalBlogContentHandler(blogEntryStartComment, blogEntryEndComment);
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            try {
                parseContext.set(HtmlMapper.class, AllTagsMapper.class.newInstance());
            } 
            catch (Exception e) {
                logger.error("error setting html mapper in the parse context", e);
                throw new RuntimeException("error setting html mapper in the parse context", e);
            } 
            HtmlParser parser = new HtmlParser();
            
            String html = page.getHTML();
            InputStream input = IOUtils.toInputStream(html);
            try {
                parser.parse(input, drupalBlogContentHandler, metadata, parseContext);
            } 
            catch (Exception e) {
                logger.error("error parsing drupal page", e);
                throw new RuntimeException("error parsing drupal page", e);
            }
    
            if (drupalBlogContentHandler.isBlogEntry()) {
                logger.debug("blog entry: " + drupalBlogContentHandler.getTitle() + " - " + 
                          drupalBlogContentHandler.getSubmissionDate() + " - " + page.getWebURL());
                
                posts.add(new TextPost(null, 
                        drupalBlogContentHandler.getTitle(), 
                        drupalBlogContentHandler.getBlogEntry(),
                        drupalBlogContentHandler.getSubmissionDate(),
                        drupalBlogContentHandler.getTags()));
            }
        }
    } 
    
    @Override
    public Object getMyLocalData() {
        return posts;
    }
    
    public static void main (String args[]) throws Exception {
        URL url = new URL("http://chrisjordan.ca/node/33");
        InputStream input = url.openStream();
        
        DrupalBlogContentHandler drupalBlogContentHandler = 
                new DrupalBlogContentHandler("##### BLOG_ENTRY_START #####", "##### BLOG_ENTRY_END #####");
        
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
    
    private String blogEntryStartComment;
    private String blogEntryEndComment;

    private boolean processingDataBlock;
    private int dataBlockTagDepth;
    private boolean processingBlogEntry;
    private int blogEntryTagDepth;
    private boolean processingTitle;
    private boolean processingSubmissionDate;
    private boolean processingTaxTerm;
    private boolean isBlogEntry;
    
    private String blogEntry;
    private StringBuffer title;
    private StringBuffer submissionDate;
    private StringBuffer currentTag;
    private List<String> tags;
    
    public DrupalBlogContentHandler (String blogEntryStartComment, String blogEntryEndComment) {
        super();
        
        this.blogEntryStartComment = blogEntryStartComment;
        this.blogEntryEndComment = blogEntryEndComment;

        processingDataBlock = false;
        dataBlockTagDepth = 0;
        processingBlogEntry = false;
        blogEntryTagDepth = 0;
        processingTitle = false;
        processingSubmissionDate = false;
        processingTaxTerm = false;
        isBlogEntry = false;
        
        blogEntry = null;
        title = new StringBuffer();
        submissionDate = new StringBuffer();
        currentTag = new StringBuffer();
        tags = new ArrayList<String>();
    }
    
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
            
            //Found submission date
            if (qName.equalsIgnoreCase("span") && StringUtils.equals(atts.getValue("class"), "submitted")) {
                processingSubmissionDate = true;
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
        
        if (processingSubmissionDate) {
            submissionDate.append(ch, start, length);
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
        
        //Submission date has ended
        if (processingSubmissionDate) {
            processingSubmissionDate = false;
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
        String[] dateTokens = submissionDate.toString().split(" ");
        if (isBlogEntry && dateTokens.length >= 4) {
            return dateTokens[1] + " " + dateTokens[3];
        }
        return null;
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