package org.chrisjordan.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TeleporterController {
    
    private String teleporterView;
    
    private final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value="/teleporter.htm")
    public String teleporter () {
        return teleporterView;
    }

    public String getTeleporterView() {
        return teleporterView;
    }

    public void setTeleporterView(String teleporterView) {
        this.teleporterView = teleporterView;
    }
}
