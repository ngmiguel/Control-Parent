package org.example.backend.controller;

import org.example.backend.service.EmploiDuTempsService;
import org.example.backend.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final EmploiDuTempsService emploiDuTempsService;

    public PdfController(PdfService pdfService, EmploiDuTempsService emploiDuTempsService) {
        this.pdfService = pdfService;
        this.emploiDuTempsService = emploiDuTempsService;
    }

    /**
     * Télécharge le bulletin de notes d'un étudiant en PDF
     * GET /api/pdf/notes/{matricule}?annee={annee}&semestre={semestre}
     */
    @GetMapping("/notes/{matricule}")
    public ResponseEntity<byte[]> telechargerNotes(
            @PathVariable String matricule,
            @RequestParam(required = false) String annee,
            @RequestParam(required = false) String semestre) {
        try {
            System.out.println("📄 Génération du bulletin de notes pour: " + matricule);
            if (annee != null && semestre != null) {
                System.out.println("   Année: " + annee + ", Semestre: " + semestre);
            }
            
            byte[] pdfBytes = pdfService.genererBulletinNotes(matricule, annee, semestre);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "bulletin_notes_" + matricule;
            if (annee != null && semestre != null) {
                filename += "_" + annee + "_" + semestre;
            }
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            System.out.println("✅ Bulletin généré avec succès (" + pdfBytes.length + " bytes)");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Télécharge l'emploi du temps d'une classe en PDF
     * GET /api/pdf/emploi-du-temps/{classeId}
     */
    @GetMapping("/emploi-du-temps/{classeId}")
    public ResponseEntity<byte[]> telechargerEmploiDuTemps(@PathVariable Long classeId) {
        try {
            System.out.println("📅 Génération de l'emploi du temps pour la classe: " + classeId);
            
            byte[] pdfBytes = emploiDuTempsService.genererEmploiDuTempsPDF(classeId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "emploi_du_temps_classe_" + classeId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            System.out.println("✅ Emploi du temps généré avec succès (" + pdfBytes.length + " bytes)");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération de l'emploi du temps: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
