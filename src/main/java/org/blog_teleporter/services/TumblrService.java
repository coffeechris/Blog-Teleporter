package org.blog_teleporter.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blog_teleporter.models.TextPost;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class TumblrService {
    private String baseURL;
    
    private final Log logger = LogFactory.getLog(getClass());

    public void createTextPost(OAuthService service, Token accessToken, String blogName, 
            String tags, String date, String title, String body) {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, baseURL + blogName + "/post");
        oauthRequest.addBodyParameter("type",  "text");
        oauthRequest.addBodyParameter("state", "published");
        oauthRequest.addBodyParameter("tags",  tags);
        oauthRequest.addBodyParameter("date",  date);

        oauthRequest.addBodyParameter("title", title);
        oauthRequest.addBodyParameter("body",  body);
        service.signRequest(accessToken, oauthRequest);
        
        Response response = oauthRequest.send();
        logger.debug(response.getBody());
    }
    
    @SuppressWarnings("unchecked")
    public List<TextPost> getTextPostsByTag(OAuthService service, Token accessToken, String blogName, String apiKey, String tag) {
        OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, baseURL + blogName + "/posts");
        oauthRequest.addQuerystringParameter("api_key", apiKey);
        oauthRequest.addQuerystringParameter("tag", tag);
        service.signRequest(accessToken, oauthRequest);
        
        Response response = oauthRequest.send();
        
        List<TextPost> textPosts = new ArrayList<TextPost>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String,Object> jsonMsg = mapper.readValue(response.getBody(), Map.class);
            Map<String,Object> tumblrResponse = (Map<String,Object>)jsonMsg.get("response");
            List<Map<String,Object>> posts = (List<Map<String,Object>>)tumblrResponse.get("posts");
            logger.debug(posts);
            
            for(Map<String,Object> post : posts) {
                if (post.get("type").equals("text")) {
                    TextPost textPost = new TextPost((Long)post.get("id"), (String)post.get("title"));
                    textPosts.add(textPost);
                }
            }
            
            logger.debug(textPosts);
        } 
        catch (IOException e) {
            logger.error("error handling posts's response", e);
            throw new RuntimeException("error handling posts's response", e);
        }
        
        return textPosts;
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
