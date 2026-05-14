package com.example.harjoitustyo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String adminEmail;

    public MailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                       @Value("${app.admin-email}") String adminEmail) {
        this.mailSenderProvider = mailSenderProvider;
        this.adminEmail = adminEmail;
    }

    public void notifyAdminNewUser(String username, String email) {
        send(adminEmail, "New CRM user registered", "User " + username + " registered with email " + email + ".");
    }

    public void sendPasswordReset(String to, String resetUrl) {
        send(to, "CRM password reset", "Open this link to reset your password: " + resetUrl);
    }

    private void send(String to, String subject, String text) {
        try {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender == null) {
                LOGGER.info("Email delivery skipped. To={}, subject={}, body={}", to, subject, text);
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception exception) {
            LOGGER.info("Email delivery skipped. To={}, subject={}, body={}", to, subject, text);
        }
    }
}
