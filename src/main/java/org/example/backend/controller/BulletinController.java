package org.example.backend.controller;

import java.util.List;

import org.example.backend.dto.MessageResponse;
import org.example.backend.dto.NoteDTO;
import org.example.backend.dto.PeriodeDTO;
import org.example.backend.service.BulletinService;
import org.example.backend.service.EtudiantService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bulletin")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class BulletinController {

    private final BulletinService bulletinService;
    private final EtudiantService etudiantService;

    public BulletinController(BulletinService bulletinService, EtudiantService etudiantService) {
        this.bulletinService = bulletinService;
        this.etudiantService = etudiantService;
    }

    /**
     * GET /api/bulletin/periodes/{matricule} Récupère les périodes disponibles
     * pour un étudiant
     */
    @GetMapping("/periodes/{matricule}")
    public ResponseEntity<?> getPeriodesDisponibles(@PathVariable String matricule) {
        try {
            List<PeriodeDTO> periodes = etudiantService.getPeriodesDisponibles(matricule);
            return ResponseEntity.ok(periodes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la récupération des périodes", false));
        }
    }

    /**
     * GET /api/bulletin/notes/{matricule} Récupère les notes d'un étudiant
     * (format JSON)
     */
    @GetMapping("/notes/{matricule}")
    public ResponseEntity<?> getNotesEtudiant(
            @PathVariable String matricule,
            @RequestParam(required = false) String anneeAcademique,
            @RequestParam(required = false) String semestre) {
        try {
            List<NoteDTO> notes;
            if (anneeAcademique != null && semestre != null) {
                notes = etudiantService.getNotesEtudiantParPeriode(matricule, anneeAcademique, semestre);
            } else {
                notes = etudiantService.getNotesEtudiant(matricule);
            }
            return ResponseEntity.ok(notes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la récupération des notes", false));
        }
    }

    /**
     * GET /api/bulletin/pdf/{matricule} Télécharge le bulletin de notes en PDF
     * Si aucune période n'est spécifiée, génère le bulletin annuel de l'année
     * en cours
     */
    @GetMapping("/pdf/{matricule}")
    public ResponseEntity<?> telechargerBulletinPDF(
            @PathVariable String matricule,
            @RequestParam(required = false) String anneeAcademique,
            @RequestParam(required = false) String semestre) {
        try {
            byte[] pdfBytes;
            if (anneeAcademique != null && semestre != null) {
                pdfBytes = bulletinService.genererBulletinPDFParPeriode(matricule, anneeAcademique, semestre);
            } else {
                // Par défaut, générer le bulletin annuel de l'année en cours
                pdfBytes = bulletinService.genererBulletinPDFParPeriode(matricule, "2024-2025", "Annuel");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bulletin_" + matricule + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur lors de la génération du bulletin PDF", false));
        }
    }
}
