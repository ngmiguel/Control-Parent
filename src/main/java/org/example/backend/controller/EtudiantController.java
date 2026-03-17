package org.example.backend.controller;

import org.example.backend.dto.EtudiantDTO;
import org.example.backend.dto.MessageResponse;
import org.example.backend.security.JwtUtil;
import org.example.backend.service.EtudiantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final JwtUtil jwtUtil;

    public EtudiantController(EtudiantService etudiantService, JwtUtil jwtUtil) {
        this.etudiantService = etudiantService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * GET /api/etudiants/mes-enfants
     * Récupère la liste des enfants du parent connecté
     */
    @GetMapping("/mes-enfants")
    public ResponseEntity<?> getMesEnfants(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraire le token
            String token = authHeader.substring(7);
            
            // Extraire le téléphone du token
            String telephone = jwtUtil.getTelephoneFromToken(token);
            
            // Récupérer les enfants
            List<EtudiantDTO> enfants = etudiantService.getEnfantsParTelephone(telephone);
            
            return ResponseEntity.ok(enfants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la récupération des enfants", false));
        }
    }

    /**
     * GET /api/etudiants/{matricule}
     * Récupère les détails d'un étudiant
     */
    @GetMapping("/{matricule}")
    public ResponseEntity<?> getEtudiant(@PathVariable String matricule) {
        try {
            var etudiant = etudiantService.getEtudiantByMatricule(matricule);
            return ResponseEntity.ok(etudiant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la récupération de l'étudiant", false));
        }
    }
}
