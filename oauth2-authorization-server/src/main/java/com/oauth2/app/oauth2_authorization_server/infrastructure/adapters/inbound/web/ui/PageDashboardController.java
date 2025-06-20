package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.ui;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class PageDashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        model.addAttribute("username", username);
        model.addAttribute("authorities", authorities);

        return "server/pages/dashboard";
    }
}
