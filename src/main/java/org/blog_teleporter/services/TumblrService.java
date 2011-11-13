package org.blog_teleporter.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class TumblrService {
    private String baseURL;
    
    private final Log logger = LogFactory.getLog(getClass());

    public void createTextPost(OAuthService service, Token accessToken, String blogName, 
            String tags, String title, String body) {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, baseURL + blogName + "/post");
        oauthRequest.addBodyParameter("type",  "text");
        oauthRequest.addBodyParameter("state", "published");
        oauthRequest.addBodyParameter("tags",  tags);
        oauthRequest.addBodyParameter("date",  "2011-11-10 10:30");

        oauthRequest.addBodyParameter("title", title);
        oauthRequest.addBodyParameter("body",  body);
        service.signRequest(accessToken, oauthRequest);
        
        Response response = oauthRequest.send();
        logger.debug(response.getBody());
    }
    
    public void getTextPostsByTag() {
        //TODO: implement
    }
    
    public void deletePost(OAuthService service, Token accessToken, String blogName, String postId) {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, baseURL + blogName + "/post/delete");
        oauthRequest.addBodyParameter("id", postId);
        service.signRequest(accessToken, oauthRequest); 
        
        Response response = oauthRequest.send();
        logger.debug(response.getBody());
    }
    
    public String getBaseURL() {
        return baseURL;
    }
    
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
