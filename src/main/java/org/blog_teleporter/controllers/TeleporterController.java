package org.blog_teleporter.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    
    private String apiKey;
    private String apiSecret;
    private String baseURL;
    
    private String teleporterView;
    
    private final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value="/teleporter.htm", method = RequestMethod.GET)
    public String teleporter (HttpServletRequest request,
                              @RequestParam(value="oauth_token", required=false)    String oauthToken,
                              @RequestParam(value="oauth_verifier", required=false) String oauthVerifier) {
        OAuthService service = (OAuthService)request.getSession().getAttribute("oauth_service");
        Token requestToken = (Token)request.getSession().getAttribute("oauth_request_token");
        
        if (StringUtils.isBlank(oauthToken) || StringUtils.isBlank(oauthVerifier) || service == null || requestToken == null) {
            return oauthRedirect(request, request.getRequestURL().toString());
        }      
        
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        
        OAuthRequest oauthRequest = new OAuthRequest(Verb.POST, baseURL + "verycrispy.tumblr.com/post/delete");
        oauthRequest.addBodyParameter("id", "11902989988");
        service.signRequest(accessToken, oauthRequest); 
        
        Response response = oauthRequest.send();
        logger.debug(response.getBody());
        
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

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    
    public String getTeleporterView() {
        return teleporterView;
    }

    public void setTeleporterView(String teleporterView) {
        this.teleporterView = teleporterView;
    }
}
