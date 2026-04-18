package com.virtualdoctor.virtual_doctor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendHighSeverityAlert(String toEmail, String patientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🚨 MediAI Health Alert — Immediate Medical Attention Required");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><head><style>" +
                    "body { font-family: Arial, sans-serif; background: #f0f4f8; margin: 0; padding: 0; }" +
                    ".container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.1); }" +
                    ".header { background: #0a1628; padding: 32px 40px; text-align: center; }" +
                    ".header h1 { color: #00c896; font-size: 24px; margin: 0; }" +
                    ".header p { color: #8892a4; margin: 8px 0 0; font-size: 14px; }" +
                    ".alert-banner { background: #fff0f3; border-left: 4px solid #ff5c7c; padding: 16px 24px; margin: 24px; border-radius: 8px; }" +
                    ".alert-banner h2 { color: #ff5c7c; margin: 0 0 6px; font-size: 18px; }" +
                    ".alert-banner p { color: #666; margin: 0; font-size: 14px; }" +
                    ".body { padding: 0 40px 32px; }" +
                    ".body p { color: #334155; font-size: 15px; line-height: 1.7; margin-bottom: 16px; }" +
                    ".steps { background: #f8fafc; border-radius: 12px; padding: 20px 24px; margin: 20px 0; }" +
                    ".steps h3 { color: #0a1628; font-size: 15px; margin: 0 0 14px; }" +
                    ".step { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 10px; }" +
                    ".step-num { background: #00c896; color: #0a1628; width: 22px; height: 22px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 700; flex-shrink: 0; }" +
                    ".step p { color: #334155; font-size: 14px; margin: 0; }" +
                    ".disclaimer { background: #fff7ed; border: 1px solid #fed7aa; border-radius: 10px; padding: 14px 18px; margin-top: 20px; }" +
                    ".disclaimer p { color: #9a3412; font-size: 13px; margin: 0; }" +
                    ".footer { background: #f8fafc; padding: 20px 40px; text-align: center; border-top: 1px solid #e2e8f0; }" +
                    ".footer p { color: #94a3b8; font-size: 12px; margin: 0; }" +
                    "</style></head><body>" +
                    "<div class='container'>" +
                    "  <div class='header'>" +
                    "    <h1>🩺 MediAI Health Alert</h1>" +
                    "    <p>AI-Powered Virtual Doctor</p>" +
                    "  </div>" +
                    "  <div class='alert-banner'>" +
                    "    <h2>🚨 High Severity Detected</h2>" +
                    "    <p>Your symptoms require immediate medical attention</p>" +
                    "  </div>" +
                    "  <div class='body'>" +
                    "    <p>Dear <strong>" + patientName + "</strong>,</p>" +
                    "    <p>Our AI doctor has analyzed your symptoms and detected a <strong style='color:#ff5c7c'>HIGH severity</strong> condition. Based on your consultation, we strongly recommend you seek immediate medical attention.</p>" +
                    "    <div class='steps'>" +
                    "      <h3>What you should do right now:</h3>" +
                    "      <div class='step'><div class='step-num'>1</div><p>Visit the nearest hospital emergency room or urgent care center immediately.</p></div>" +
                    "      <div class='step'><div class='step-num'>2</div><p>Call emergency services (102 / 112) if your condition is rapidly worsening.</p></div>" +
                    "      <div class='step'><div class='step-num'>3</div><p>Inform a family member or trusted person about your condition.</p></div>" +
                    "      <div class='step'><div class='step-num'>4</div><p>Bring your MediAI consultation report to show the doctor.</p></div>" +
                    "    </div>" +
                    "    <div class='disclaimer'>" +
                    "      <p>⚠️ <strong>Important:</strong> MediAI provides AI-powered guidance only. This alert is NOT a substitute for professional medical diagnosis. Please consult a qualified doctor immediately.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "  <div class='footer'>" +
                    "    <p>This alert was sent automatically by MediAI · Please do not reply to this email</p>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("HIGH severity alert email sent to: " + toEmail);

        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}