package org.blog_teleporter.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blog_teleporter.services.TumblrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TeleporterController {
    
    private String teleporterView;
    
    private TumblrService tumblrService;
    
    private final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value="/teleporter.htm")
    public String teleporter () {
        tumblrService.blogInfo("verycrispy.tumblr.com");
        
        return teleporterView;
    }

    public String getTeleporterView() {
        return teleporterView;
    }

    public void setTeleporterView(String teleporterView) {
        this.teleporterView = teleporterView;
    }

    public TumblrService getTumblrService() {
        return tumblrService;
    }

    public void setTumblrService(TumblrService tumblrService) {
        this.tumblrService = tumblrService;
    }
}
