package com.tis.notificationservice.services.email;

import org.thymeleaf.context.Context;

public interface IEmailService {
    void sendEmail(String to, String subject, String template, Context context);
}
