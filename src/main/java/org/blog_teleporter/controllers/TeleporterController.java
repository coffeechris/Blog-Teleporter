package org.blog_teleporter.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blog_teleporter.services.TumblrService;
import org.blog_teleporter.utils.TumblrAPI;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TeleporterController {
    
    private TumblrService tumblrService;
    
    private String apiKey;
    private String apiSecret;
    
    private String teleporterView;
    
    private final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value="/teleporter.htm", method = RequestMethod.GET)
    public String teleporter (HttpServletRequest request,
                              @RequestParam(value="oauth_token", required=false)    String oauthToken,
                              @RequestParam(value="oauth_verifier", required=false) String oauthVerifier) {
        OAuthService service = (OAuthService)request.getSession().getAttribute("oauth_service");
        Token requestToken = (Token)request.getSession().getAttribute("oauth_request_token");
        Token accessToken  = (Token)request.getSession().getAttribute("oauth_access_token");
        
        if (service == null || requestToken == null) {
            logger.debug("oauth required - redirecting");
            return oauthRedirect(request, request.getRequestURL().toString());
        }      
        
        if (accessToken == null) {
            logger.debug("initializing access token");
            Verifier verifier = new Verifier(oauthVerifier);
            accessToken = service.getAccessToken(requestToken, verifier);
            request.getSession().setAttribute("oauth_access_token", accessToken);
        }
        
        tumblrService.deletePost(service, accessToken, "verycrispy.tumblr.com", "11902989988");
        
        tumblrService.createTextPost(service, accessToken, "verycrispy.tumblr.com", "teleporter", "2011-11-10 10:30", "a test title 1", "a test body 1");
        tumblrService.createTextPost(service, accessToken, "verycrispy.tumblr.com", "teleporter", "2011-11-10 10:30", "a test title 2", "a test body 2");

        return teleporterView;
    }

    private String oauthRedirect (HttpServletRequest request, String callbackURL) {
        OAuthService service = new ServiceBuilder().provider(TumblrAPI.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(callbackURL)
                .signatureType(SignatureType.QueryString)
                .build();
        Token requestToken = service.getRequestToken();
        
        request.getSession().setAttribute("oauth_service", service);
        request.getSession().setAttribute("oauth_request_token", requestToken);
        
        String authUrl = service.getAuthorizationUrl(requestToken);

        return "redirect:" + authUrl;
    }
    
    public TumblrService getTumblrService() {
        return tumblrService;
    }

    public void setTumblrService(TumblrService tumblrService) {
        this.tumblrService = tumblrService;
    }
    
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
    public String getTeleporterView() {
        return teleporterView;
    }

    public void setTeleporterView(String teleporterView) {
        this.teleporterView = teleporterView;
    }
}
