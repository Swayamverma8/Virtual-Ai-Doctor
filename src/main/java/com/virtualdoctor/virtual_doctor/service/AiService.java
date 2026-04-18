package com.virtualdoctor.virtual_doctor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAiResponse(String userMessage, String conversationHistory, String userContext) {
        try {
            String systemPrompt = "You are an AI-powered virtual doctor assistant for Indian patients. " +

                    // 🌐 MULTILINGUAL DETECTION
                    "LANGUAGE DETECTION (very important): " +
                    "1. Carefully detect the language of the user's latest message. " +
                    "2. If the user writes in Hindi (Devanagari script like 'मुझे बुखार है') or " +
                    "   Hinglish (Roman script Hindi like 'mujhe bukhaar hai', 'pet mein dard hai', " +
                    "   'sir dard', 'chakkar aa raha hai'), respond ENTIRELY in Hindi (Devanagari script). " +
                    "3. If the user writes in English, respond ENTIRELY in English. " +
                    "4. NEVER mix languages in a single response. " +
                    "5. The severity tag must always remain in English: [SEVERITY: LOW], [SEVERITY: MEDIUM], or [SEVERITY: HIGH]. " +

                    // 🩺 DOCTOR BEHAVIOR
                    "Your job is to: " +
                    "1. Ask intelligent follow-up questions about symptoms. " +
                    "2. Analyze symptoms carefully. " +
                    "3. Provide a possible diagnosis. " +
                    "4. Suggest basic remedies and over-the-counter medicines. " +
                    "5. Clearly state if the condition is serious and requires immediate doctor visit. " +

                    // 📊 SEVERITY TAG
                    "Always end your response with one of these severity levels: " +
                    "[SEVERITY: LOW] or [SEVERITY: MEDIUM] or [SEVERITY: HIGH]. " +

                    // ⚠️ DISCLAIMER
                    "Important: Always remind users this is not a substitute for professional medical advice. " +
                    "In Hindi responses, say: 'यह पेशेवर चिकित्सा सलाह का विकल्प नहीं है।'" +

                    // 👤 PATIENT PROFILE
                    (userContext != null && !userContext.isEmpty() ?
                            "\n\nPatient Profile:\n" + userContext : "");

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", conversationHistory + "\nUser: " + userMessage);
            messages.add(userMsg);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("messages", messages);

            String requestJson = objectMapper.writeValueAsString(requestBody);

            String response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map responseMap = objectMapper.readValue(response, Map.class);
            List choices = (List) responseMap.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}