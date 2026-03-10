package org.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${sms.provider}")
    private String provider;

    @Value("${sms.sender.id}")
    private String senderId;

    @Autowired(required = false)
    private TwilioSmsService twilioSmsService;

    /**
     * Envoie un SMS avec le code de validation
     */
    public void envoyerCodeValidation(String telephone, String code) {
        String message = String.format(
            "Votre code de validation %s est: %s. Valide pendant 15 minutes.",
            senderId,
            code
        );
        
        if ("console".equalsIgnoreCase(provider)) {
            // Mode développement : afficher dans la console
            System.out.println("===========================================");
            System.out.println("📱 SMS ENVOYÉ À: " + telephone);
            System.out.println("🔐 CODE DE VALIDATION: " + code);
            System.out.println("⏱️  Expiration: 15 minutes");
            System.out.println("===========================================");
        } else if ("twilio".equalsIgnoreCase(provider)) {
            // Mode production avec Twilio
            if (twilioSmsService == null) {
                throw new RuntimeException("TwilioSmsService n'est pas configuré");
            }
            String telNormalise = normaliserTelephone(telephone);
            System.out.println("📤 Envoi du SMS via Twilio à: " + telNormalise);
            twilioSmsService.envoyerSms(telNormalise, message);
        } else {
            throw new RuntimeException("Fournisseur SMS non supporté: " + provider);
        }
    }

    /**
     * Nettoie et normalise le numéro de téléphone
     */
    public String normaliserTelephone(String telephone) {
        // Supprimer les espaces, parenthèses, tirets
        String cleaned = telephone.replaceAll("[\\s()\\-]", "");
        
        // Si le numéro commence par 6 (format camerounais), ajouter +237
        if (cleaned.matches("^6[0-9]{8}$")) {
            cleaned = "+237" + cleaned;
        }
        
        // Si le numéro commence par 237 sans +, ajouter le +
        if (cleaned.matches("^237[0-9]{9}$")) {
            cleaned = "+" + cleaned;
        }
        
        return cleaned;
    }
}
