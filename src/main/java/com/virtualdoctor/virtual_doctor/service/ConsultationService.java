package com.virtualdoctor.virtual_doctor.service;

import com.virtualdoctor.virtual_doctor.model.Message;
import com.virtualdoctor.virtual_doctor.model.Session;
import com.virtualdoctor.virtual_doctor.model.User;
import com.virtualdoctor.virtual_doctor.repository.MessageRepository;
import com.virtualdoctor.virtual_doctor.repository.SessionRepository;
import com.virtualdoctor.virtual_doctor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiService aiService;

    @Autowired
    private EmailService emailService; // ✅ ADDED

    public Session startSession(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Session session = new Session();
        session.setUser(user);
        return sessionRepository.save(session);
    }

    public String chat(Long sessionId, String userMessage, String email) {
        Session session = sessionRepository.findById(sessionId).orElseThrow();

        Message userMsg = new Message();
        userMsg.setSession(session);
        userMsg.setSender("user");
        userMsg.setContent(userMessage);
        messageRepository.save(userMsg);

        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        StringBuilder conversationHistory = new StringBuilder();
        for (Message msg : history) {
            conversationHistory.append(msg.getSender())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        // Build user profile context
        User user = userRepository.findByEmail(email).orElseThrow();
        StringBuilder userContext = new StringBuilder();
        if (user.getAge() != null)
            userContext.append("Age: ").append(user.getAge()).append("\n");
        if (user.getBloodGroup() != null && !user.getBloodGroup().isEmpty())
            userContext.append("Blood Group: ").append(user.getBloodGroup()).append("\n");
        if (user.getAllergies() != null && !user.getAllergies().isEmpty())
            userContext.append("Known Allergies: ").append(user.getAllergies()).append("\n");
        if (user.getMedicalHistory() != null && !user.getMedicalHistory().isEmpty())
            userContext.append("Medical History: ").append(user.getMedicalHistory()).append("\n");

        String aiResponse = aiService.getAiResponse(
                userMessage,
                conversationHistory.toString(),
                userContext.toString()
        );

        Message aiMsg = new Message();
        aiMsg.setSession(session);
        aiMsg.setSender("ai");
        aiMsg.setContent(aiResponse);
        messageRepository.save(aiMsg);

        // ✅ Severity detection + email trigger
        if (aiResponse.contains("[SEVERITY: HIGH]")) {
            session.setSeverity("HIGH");
            emailService.sendHighSeverityAlert(user.getEmail(), user.getName()); // ✅ ADDED
        } else if (aiResponse.contains("[SEVERITY: MEDIUM]")) {
            session.setSeverity("MEDIUM");
        } else {
            session.setSeverity("LOW");
        }

        session.setDiagnosis(aiResponse.substring(0, Math.min(aiResponse.length(), 500)));
        sessionRepository.save(session);

        return aiResponse;
    }

    public List<Session> getHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Message> getMessages(Long sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}