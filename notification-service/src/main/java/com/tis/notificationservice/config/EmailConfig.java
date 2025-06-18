package com.tis.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email.template")
public class EmailConfig {

    // Información de la empresa
    private String companyName = "Company";
    private String teamName = "El Equipo de Company";
    private String address = "101 King St, San Francisco, CA 94107";
    private String logoUrl = "https://asset.cloudinary.com/dcg6envhf/d7e6b71d9287a5a0e3b94c0f33c0c13c";

    // Enlaces de redes sociales
    private SocialLinks socialLinks = new SocialLinks();

    // Enlaces del footer
    private FooterLinks footerLinks = new FooterLinks();

    // Getters y Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    public FooterLinks getFooterLinks() {
        return footerLinks;
    }

    public void setFooterLinks(FooterLinks footerLinks) {
        this.footerLinks = footerLinks;
    }

    // Clase interna para enlaces de redes sociales
    public static class SocialLinks {
        private String facebook;
        private String tiktok;
        private String whatsapp;
        private String linkedin;

        // Método helper para verificar si hay redes sociales configuradas
        public boolean hasSocialLinks() {
            return (facebook != null && !facebook.isEmpty()) ||
                    (tiktok != null && !tiktok.isEmpty()) ||
                    (whatsapp != null && !whatsapp.isEmpty()) ||
                    (linkedin != null && !linkedin.isEmpty());
        }

        // Getters y Setters
        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String facebook) {
            this.facebook = facebook;
        }

        public String getTiktok() {
            return tiktok;
        }

        public void setTiktok(String tiktok) {
            this.tiktok = tiktok;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public void setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
        }

        public String getLinkedin() {
            return linkedin;
        }

        public void setLinkedin(String linkedin) {
            this.linkedin = linkedin;
        }
    }

    // Clase interna para enlaces del footer
    public static class FooterLinks {
        private String privacy = "https://tuempresa.com/privacy";
        private String support = "https://tuempresa.com/support";

        // Getters y Setters
        public String getPrivacy() {
            return privacy;
        }

        public void setPrivacy(String privacy) {
            this.privacy = privacy;
        }

        public String getSupport() {
            return support;
        }

        public void setSupport(String support) {
            this.support = support;
        }
    }
}