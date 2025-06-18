package com.oauth2.app.oauth2_authorization_server.controller.v1;

import com.oauth2.app.oauth2_authorization_server.dto.request.clientOauth2.OAuth2ClientRegistrationRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.OAuth2ClientRegistrationResponse;
import com.oauth2.app.oauth2_authorization_server.service.oauth.OAuth2ClientRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageClientController {


    private final OAuth2ClientRegistrationService oAuth2ClientRegistrationService;

    @GetMapping("/client")
    public String showClientPage(Model model) {
        List<OAuth2ClientRegistrationResponse> clients = oAuth2ClientRegistrationService.fetchOauth2Clients();
        model.addAttribute("clients", clients);
        model.addAttribute("client", new OAuth2ClientRegistrationRequest());
        return "server/pages/client";
    }

    @PostMapping("/save-client")
    public String saveClient(@ModelAttribute OAuth2ClientRegistrationRequest client,
                             RedirectAttributes redirectAttributes) {
        try {
            oAuth2ClientRegistrationService.save(client);
            redirectAttributes.addFlashAttribute("successMessage", "Client saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving client: " + e.getMessage());
        }
        return "redirect:/client";
    }

    @PostMapping("/delete-client/{id}")
    public String deleteClient(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            oAuth2ClientRegistrationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Client deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting client: " + e.getMessage());
        }
        return "redirect:/client";
    }
}
