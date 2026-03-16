package com.project.lms.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendWelcomeEmail(String toEmail, String name) {

        try {
            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("welcome-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to LMS 🎉");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Mail sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCoursePublishedEmail(String toEmail, String name, String courseTitle) {

        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("courseTitle", courseTitle);

            String htmlContent = templateEngine.process("course-published", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Course Published!");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Course publish email sent");

        } catch (Exception e) {
            System.out.println("Failed to send course publish email");
            e.printStackTrace();
        }
    }
}