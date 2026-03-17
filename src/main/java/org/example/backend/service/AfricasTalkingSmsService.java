package org.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@Service
public class AfricasTalkingSmsService {

    @Value("${sms.africastalking.username:}")
    private String username;

    @Value("${sms.africastalking.api.key:}")
    private String apiKey;

    @Value("${sms.africastalking.sender.id:}")
    private String senderId;

    private RestTemplate restTemplate;
    private boolean initialized = false;
    private static final String SMS_URL = "https://api.africastalking.com/version1/messaging";

    @PostConstruct
    private void initialize() {
        if (username != null && !username.isEmpty() && apiKey != null && !apiKey.isEmpty()) {
            restTemplate = new RestTemplate();
            initialized = true;
            System.out.println("✅ Africa's Talking initialisé avec succès");
            System.out.println("   → Username: " + username);
            System.out.println("   → Sender ID: " + senderId);
        }
    }

    public void envoyerSms(String to, String message) {
        if (!initialized) {
            throw new RuntimeException("Africa's Talking n'est pas configuré. Vérifiez vos identifiants dans .env");
        }

        try {
            // Africa's Talking accepte les numéros au format +237XXXXXXXXX
            if (!to.startsWith("+")) {
                to = "+" + to;
            }

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("apiKey", apiKey);
            headers.set("Accept", "application/json");

            // Préparer le body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("username", username);
            body.add("to", to);
            body.add("message", message);
            // Sender ID (nom affiché sur le téléphone du destinataire)
            if (senderId != null && !senderId.isEmpty()) {
                body.add("from", senderId);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // Envoyer la requête
            ResponseEntity<String> response = restTemplate.postForEntity(SMS_URL, request, String.class);

            System.out.println("✅ SMS envoyé avec succès via Africa's Talking");
            System.out.println("   → Destinataire: " + to);
            System.out.println("   → Message: " + message);
            System.out.println("   → Réponse: " + response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi du SMS via Africa's Talking: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Échec de l'envoi du SMS: " + e.getMessage(), e);
        }
    }
}
