package org.example.backend.service;

import org.example.backend.entity.CodeValidation;
import org.example.backend.entity.Etudiant;
import org.example.backend.repository.CodeValidationRepository;
import org.example.backend.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AuthService {

    private final CodeValidationRepository codeValidationRepository;
    private final EtudiantRepository etudiantRepository;
    private final SmsService smsService;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    @Value("${otp.length}")
    private int otpLength;

    public AuthService(CodeValidationRepository codeValidationRepository,
            EtudiantRepository etudiantRepository,
            SmsService smsService) {
        this.codeValidationRepository = codeValidationRepository;
        this.etudiantRepository = etudiantRepository;
        this.smsService = smsService;
    }

    /**
     * Génère un code OTP aléatoire
     */
    public String genererCodeOTP() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Vérifie si le numéro de téléphone appartient à un parent
     */
    public boolean estParentValide(String telephone) {
        String telNormalise = smsService.normaliserTelephone(telephone);

        // Vérifier si le téléphone existe dans telephonePere ou telephoneMere
        List<Etudiant> etudiants = etudiantRepository
                .findByTelephonePereContainingOrTelephoneMereContaining(telNormalise, telNormalise);

        // Vérifier aussi sans normalisation pour les formats multiples
        if (etudiants.isEmpty()) {
            etudiants = etudiantRepository
                    .findByTelephonePereContainingOrTelephoneMereContaining(telephone, telephone);
        }

        return !etudiants.isEmpty();
    }

    /**
     * Demande un code de validation et l'envoie par SMS
     */
    @Transactional
    public void demanderCodeValidation(String telephone) {
        // Vérifier si le parent existe
        if (!estParentValide(telephone)) {
            throw new IllegalArgumentException("Aucun enfant trouvé pour ce numéro de téléphone");
        }

        // Générer le code
        String code = genererCodeOTP();

        // Créer l'entité CodeValidation
        CodeValidation codeValidation = new CodeValidation();
        codeValidation.setTelephone(telephone);
        codeValidation.setCode(code);
        codeValidation.setDateCreation(LocalDateTime.now());
        codeValidation.setDateExpiration(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        codeValidation.setUtilise(false);

        // Sauvegarder en base
        codeValidationRepository.save(codeValidation);

        // Envoyer le SMS
        smsService.envoyerCodeValidation(telephone, code);
    }

    /**
     * Vérifie le code de validation
     */
    @Transactional
    public boolean verifierCode(String telephone, String code) {
        // Rechercher le code non utilisé
        var codeValidationOpt = codeValidationRepository
                .findByTelephoneAndCodeAndUtiliseFalse(telephone, code);

        if (codeValidationOpt.isEmpty()) {
            return false;
        }

        CodeValidation codeValidation = codeValidationOpt.get();

        // Vérifier l'expiration
        if (LocalDateTime.now().isAfter(codeValidation.getDateExpiration())) {
            return false;
        }

        // Marquer comme utilisé
        codeValidation.setUtilise(true);
        codeValidationRepository.save(codeValidation);

        return true;
    }

    /**
     * Nettoie les codes expirés (à exécuter périodiquement)
     */
    @Transactional
    public void nettoyerCodesExpires() {
        // TODO: Implémenter une tâche planifiée pour supprimer les codes expirés
        // @Scheduled(cron = "0 0 * * * *") // Toutes les heures
    }
}
