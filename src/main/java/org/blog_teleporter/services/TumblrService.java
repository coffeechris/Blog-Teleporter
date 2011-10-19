package org.blog_teleporter.services;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth.consumer.OAuthRestTemplate;

public class TumblrService {
    private String baseURL;
    private String apiKey;
    
    private OAuthRestTemplate tumblrRestTemplate;

    private final Log logger = LogFactory.getLog(getClass());
    
    public String blogInfo(String tumblrHostname) {
        String infoJSON = tumblrRestTemplate.getForObject(URI.create(baseURL + tumblrHostname + "/followers"), String.class);
        
        logger.debug(infoJSON);
        
        return infoJSON;
    }
    
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public OAuthRestTemplate getTumblrRestTemplate() {
        return tumblrRestTemplate;
    }

    public void setTumblrRestTemplate(OAuthRestTemplate tumblrRestTemplate) {
        this.tumblrRestTemplate = tumblrRestTemplate;
    }

    
}
