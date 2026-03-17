package org.example.backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    @Value("${sms.twilio.account.sid:}")
    private String accountSid;

    @Value("${sms.twilio.auth.token:}")
    private String authToken;

    @Value("${sms.twilio.phone.number:}")
    private String fromPhoneNumber;

    private boolean initialized = false;

    private void initialize() {
        if (!initialized && accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
            initialized = true;
            System.out.println("✅ Twilio initialisé avec succès");
        }
    }

    public void envoyerSms(String to, String message) {
        initialize();
        
        if (!initialized) {
            throw new RuntimeException("Twilio n'est pas configuré. Vérifiez vos identifiants dans application.properties");
        }
        
        try {
            Message twilioMessage = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromPhoneNumber),
                message
            ).create();
            
            System.out.println("✅ SMS envoyé avec succès via Twilio");
            System.out.println("   → Destinataire: " + to);
            System.out.println("   → SID: " + twilioMessage.getSid());
            System.out.println("   → Statut: " + twilioMessage.getStatus());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi du SMS via Twilio: " + e.getMessage());
            throw new RuntimeException("Échec de l'envoi du SMS: " + e.getMessage(), e);
        }
    }
}
