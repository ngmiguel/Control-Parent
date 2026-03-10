package org.example.backend.controller;

import org.example.backend.dto.EmploiDuTempsDTO;
import org.example.backend.dto.MessageResponse;
import org.example.backend.service.EmploiDuTempsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emploi-du-temps")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class EmploiDuTempsController {

    private final EmploiDuTempsService emploiDuTempsService;

    public EmploiDuTempsController(EmploiDuTempsService emploiDuTempsService) {
        this.emploiDuTempsService = emploiDuTempsService;
    }

    /**
     * GET /api/emploi-du-temps/classe/{classeId}
     * Récupère l'emploi du temps d'une classe (format JSON)
     */
    @GetMapping("/classe/{classeId}")
    public ResponseEntity<?> getEmploiDuTemps(@PathVariable Long classeId) {
        try {
            EmploiDuTempsDTO emploi = emploiDuTempsService.getEmploiDuTempsParClasse(classeId);
            return ResponseEntity.ok(emploi);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la récupération de l'emploi du temps", false));
        }
    }

    /**
     * GET /api/emploi-du-temps/pdf/{classeId}
     * Télécharge l'emploi du temps en PDF
     */
    @GetMapping("/pdf/{classeId}")
    public ResponseEntity<?> telechargerEmploiDuTempsPDF(@PathVariable Long classeId) {
        try {
            byte[] pdfBytes = emploiDuTempsService.genererEmploiDuTempsPDF(classeId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "emploi_du_temps_classe_" + classeId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la génération de l'emploi du temps PDF", false));
        }
    }
}
