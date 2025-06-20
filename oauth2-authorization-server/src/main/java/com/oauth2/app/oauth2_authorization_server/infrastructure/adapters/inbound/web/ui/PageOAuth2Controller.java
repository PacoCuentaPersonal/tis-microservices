package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageOAuth2Controller {

    @GetMapping(value = "/login")
    public String customLoginPageOauth2(){
        return "server/pages/login";
    }

}
