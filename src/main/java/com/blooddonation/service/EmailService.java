package com.blooddonation.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

/**
 * Sends emails through Gmail SMTP (configured in application.properties).
 * Failures are logged, never thrown - so a mail problem can't break a page
 * or reveal whether an email address is registered.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.mail.from-name:LifeStream Blood Donation}")
    private String fromName;

    public void sendPasswordResetEmail(String to, String recipientName, String accountLabel,
                                       String resetLink, int expiryMinutes) {
        String subject = "Reset your LifeStream password";

        String plain = "Hello " + recipientName + ",\n\n"
                + "We received a request to reset the password of your LifeStream " + accountLabel + " account.\n\n"
                + "Open this link to choose a new password (valid for " + expiryMinutes + " minutes):\n"
                + resetLink + "\n\n"
                + "If you did not request this, you can safely ignore this email - your password will not change.\n\n"
                + "- LifeStream Blood Donation";

        String html = buildResetHtml(recipientName, accountLabel, resetLink, expiryMinutes);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plain, html);
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Could not send password reset email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildResetHtml(String recipientName, String accountLabel, String resetLink, int expiryMinutes) {
        String name = HtmlUtils.htmlEscape(recipientName == null ? "" : recipientName);
        String label = HtmlUtils.htmlEscape(accountLabel);
        String link = HtmlUtils.htmlEscape(resetLink);

        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f4f4f5;font-family:Arial,Helvetica,sans-serif;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f4f4f5;padding:32px 12px;">
                    <tr><td align="center">
                      <table role="presentation" width="480" cellspacing="0" cellpadding="0" style="max-width:480px;width:100%%;background:#ffffff;border-radius:12px;overflow:hidden;">
                        <tr><td style="background:#b7131a;padding:22px 28px;color:#ffffff;font-size:20px;font-weight:bold;">
                          &#10084; LifeStream
                        </td></tr>
                        <tr><td style="padding:28px;color:#1a1c1c;font-size:15px;line-height:1.6;">
                          <p style="margin:0 0 12px;">Hello %s,</p>
                          <p style="margin:0 0 20px;">We received a request to reset the password of your LifeStream <strong>%s</strong> account.</p>
                          <p style="margin:0 0 24px;text-align:center;">
                            <a href="%s" style="background:#b7131a;color:#ffffff;text-decoration:none;padding:12px 28px;border-radius:8px;font-weight:bold;display:inline-block;">Reset Password</a>
                          </p>
                          <p style="margin:0 0 8px;font-size:13px;color:#555;">This link is valid for %d minutes and can be used only once.</p>
                          <p style="margin:0 0 8px;font-size:13px;color:#555;">If the button doesn't work, copy and paste this link into your browser:</p>
                          <p style="margin:0 0 20px;font-size:12px;word-break:break-all;color:#005cab;">%s</p>
                          <hr style="border:none;border-top:1px solid #e5e5e5;margin:20px 0;">
                          <p style="margin:0;font-size:12px;color:#777;">If you did not request this, you can safely ignore this email &mdash; your password will not change.</p>
                        </td></tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(name, label, link, expiryMinutes, link);
    }
}
