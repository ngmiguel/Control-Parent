package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.AuthResponse;
import org.example.backend.dto.DemandeCodeRequest;
import org.example.backend.dto.MessageResponse;
import org.example.backend.dto.VerificationCodeRequest;
import org.example.backend.security.JwtUtil;
import org.example.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint pour demander un code de validation
     * POST /api/auth/demander-code
     */
    @PostMapping("/demander-code")
    public ResponseEntity<?> demanderCode(@Valid @RequestBody DemandeCodeRequest request) {
        try {
            authService.demanderCodeValidation(request.getTelephone());
            return ResponseEntity.ok(new MessageResponse(
                "Code de validation envoyé par SMS. Valide pendant 15 minutes.",
                true
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de l'envoi du code", false));
        }
    }

    /**
     * Endpoint pour vérifier le code et obtenir un token JWT
     * POST /api/auth/verifier-code
     */
    @PostMapping("/verifier-code")
    public ResponseEntity<?> verifierCode(@Valid @RequestBody VerificationCodeRequest request) {
        try {
            boolean codeValide = authService.verifierCode(request.getTelephone(), request.getCode());
            
            if (codeValide) {
                // Générer le token JWT
                String token = jwtUtil.generateToken(request.getTelephone());
                
                return ResponseEntity.ok(new AuthResponse(
                    token,
                    request.getTelephone(),
                    "Authentification réussie"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Code invalide ou expiré", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la vérification du code", false));
        }
    }

    /**
     * Endpoint pour se déconnecter (côté client, supprimer le token)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new MessageResponse("Déconnexion réussie", true));
    }

    /**
     * Endpoint de test pour vérifier si l'utilisateur est authentifié
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String telephone = jwtUtil.getTelephoneFromToken(token);
            return ResponseEntity.ok(new MessageResponse("Utilisateur: " + telephone, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Non authentifié", false));
        }
    }
}
