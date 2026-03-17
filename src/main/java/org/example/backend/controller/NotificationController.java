package org.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.example.backend.entity.Notification;
import org.example.backend.security.JwtUtil;
import org.example.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // ouvert pour les apps externes
public class NotificationController {

    private final NotificationService service;
    private final JwtUtil jwtUtil;

    @Value("${notifications.api.key:changeme-secret-key}")
    private String apiKey;

    public NotificationController(NotificationService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    /**
     * SSE : accepte le token en header OU en query param (EventSource ne
     * supporte pas les headers)
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7) : tokenParam;
        return service.subscribe(jwtUtil.getTelephoneFromToken(token));
    }

    /**
     * Récupérer toutes les notifications du parent
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAll(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(service.getNotifications(extraireTelephone(authHeader)));
    }

    /**
     * Nombre de notifications non lues
     */
    @GetMapping("/non-lues")
    public ResponseEntity<Map<String, Long>> getNonLues(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(Map.of("count", service.getNonLues(extraireTelephone(authHeader))));
    }

    /**
     * Marquer toutes comme lues
     */
    @PutMapping("/lire-tout")
    public ResponseEntity<Void> marquerToutesCommeLues(@RequestHeader("Authorization") String authHeader) {
        service.marquerToutesCommeLues(extraireTelephone(authHeader));
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint webhook : n'importe quelle app externe peut envoyer une
     * notification en passant le header X-API-Key
     */
    @PostMapping("/envoyer")
    public ResponseEntity<?> envoyer(
            @RequestHeader(value = "X-API-Key", required = false) String key,
            @RequestBody Map<String, String> body) {

        if (key == null || !key.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Clé API invalide ou manquante"));
        }

        String telephone = body.get("telephone");
        if (telephone == null || telephone.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le champ 'telephone' est requis"));
        }

        try {
            Notification notif = service.creer(
                    telephone,
                    body.getOrDefault("titre", "Notification"),
                    body.getOrDefault("message", ""),
                    body.getOrDefault("type", "EVENEMENT")
            );
            return ResponseEntity.ok(notif);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    private String extraireTelephone(String authHeader) {
        return jwtUtil.getTelephoneFromToken(authHeader.substring(7));
    }
}
