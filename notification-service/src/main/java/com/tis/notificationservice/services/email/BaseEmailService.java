package com.tis.notificationservice.services.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public abstract class BaseEmailService {
    protected final JavaMailSender javaMailSender;
    protected final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    protected String sender;

    @Value("${app.base-url:https://tuempresa.com}")
    protected String baseUrl;

    public BaseEmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Método base para enviar cualquier tipo de email
     */
    protected void sendEmail(String to, String subject, String template, Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String htmlContent = templateEngine.process(template, context);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(sender);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
}