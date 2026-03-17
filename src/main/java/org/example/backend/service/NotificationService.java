package org.example.backend.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.example.backend.entity.Notification;
import org.example.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    // Map téléphone -> liste d'émetteurs SSE actifs
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public SseEmitter subscribe(String telephone) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(telephone, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(telephone, emitter));
        emitter.onTimeout(() -> removeEmitter(telephone, emitter));
        emitter.onError(e -> removeEmitter(telephone, emitter));

        // Envoyer un ping initial pour confirmer la connexion
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (Exception e) {
            removeEmitter(telephone, emitter);
        }

        return emitter;
    }

    private static final java.util.Set<String> TYPES_VALIDES
            = java.util.Set.of("REUNION", "RESULTATS", "ABSENCE", "PAIEMENT", "EVENEMENT");

    public Notification creer(String telephone, String titre, String message, String type) {
        String tel = normaliser(telephone);
        String typeNorm = type != null ? type.toUpperCase().trim() : "EVENEMENT";
        if (!TYPES_VALIDES.contains(typeNorm)) {
            throw new IllegalArgumentException("Type invalide. Valeurs acceptées : REUNION, RESULTATS, ABSENCE, PAIEMENT, EVENEMENT");
        }
        Notification notif = new Notification(tel, titre, message, typeNorm);
        notif = repository.save(notif);
        diffuser(tel, notif);
        return notif;
    }

    public List<Notification> getNotifications(String telephone) {
        return repository.findByTelephoneOrderByDateCreationDesc(normaliser(telephone));
    }

    public long getNonLues(String telephone) {
        return repository.countByTelephoneAndLuFalse(normaliser(telephone));
    }

    public void marquerToutesCommeLues(String telephone) {
        repository.marquerToutesCommeLues(normaliser(telephone));
    }

    /**
     * Normalise le numéro : supprime +237 ou 237 en tête, garde les 9 chiffres
     * locaux. Ex: +237658237678 → 658237678 | 237658237678 → 658237678 |
     * 658237678 → 658237678
     */
    private String normaliser(String telephone) {
        if (telephone == null) {
            return null;
        }
        String t = telephone.trim().replaceAll("\\s+", "");
        if (t.startsWith("+237")) {
            return t.substring(4);
        }
        if (t.startsWith("237") && t.length() > 9) {
            return t.substring(3);
        }
        return t;
    }

    private void diffuser(String telephone, Notification notif) {
        List<SseEmitter> liste = emitters.get(telephone);
        if (liste == null) {
            return;
        }

        List<SseEmitter> mortes = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : liste) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notif));
            } catch (Exception e) {
                mortes.add(emitter);
            }
        }
        liste.removeAll(mortes);
    }

    private void removeEmitter(String telephone, SseEmitter emitter) {
        List<SseEmitter> liste = emitters.get(telephone);
        if (liste != null) {
            liste.remove(emitter);
        }
    }
}
