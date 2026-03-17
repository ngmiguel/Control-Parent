package org.example.backend.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.example.backend.dto.MatiereNotesDTO;
import org.example.backend.entity.Etudiant;
import org.example.backend.entity.Inscription;
import org.example.backend.entity.Note;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class PdfService {

    private final EtudiantService etudiantService;

    public PdfService(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    /**
     * Génère le bulletin de notes d'un étudiant en PDF
     */
    public byte[] genererBulletinNotes(String matricule) {
        // Par défaut, utiliser l'année et le semestre les plus récents
        return genererBulletinNotes(matricule, null, null);
    }
    
    /**
     * Génère le bulletin de notes d'un étudiant en PDF pour une année et un semestre spécifiques
     */
    public byte[] genererBulletinNotes(String matricule, String anneeAcademique, String semestre) {
        try {
            // Récupérer l'étudiant
            Etudiant etudiant = etudiantService.getEtudiantByMatricule(matricule);
            
            // Préparer les données
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("matricule", etudiant.getMatricule());
            parameters.put("nom", etudiant.getNom());
            parameters.put("prenom", etudiant.getPrenom());
            parameters.put("classe", etudiant.getClasse().getLibelle());
            parameters.put("filiere", etudiant.getClasse().getFiliere());
            parameters.put("niveau", etudiant.getClasse().getNiveau());
            parameters.put("dateGeneration", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            parameters.put("rang", etudiant.getRang() != null ? etudiant.getRang().toString() : "N/A");
            
            // Ajouter l'année académique et le semestre sélectionnés
            if (anneeAcademique != null && !anneeAcademique.isEmpty()) {
                parameters.put("anneeAcademique", anneeAcademique);
            } else {
                // Par défaut, prendre l'année la plus récente
                String anneeParDefaut = etudiant.getInscriptions().stream()
                        .map(Inscription::getAnneeAcademique)
                        .distinct()
                        .max(String::compareTo)
                        .orElse("2024-2025");
                parameters.put("anneeAcademique", anneeParDefaut);
            }
            
            if (semestre != null && !semestre.isEmpty()) {
                parameters.put("semestre", semestre);
            } else {
                parameters.put("semestre", "Tous les semestres");
            }
            
            // Filtrer les inscriptions par année et semestre si spécifiés
            List<Inscription> inscriptionsFiltrees = etudiant.getInscriptions();
            if (anneeAcademique != null && semestre != null) {
                inscriptionsFiltrees = inscriptionsFiltrees.stream()
                        .filter(i -> i.getAnneeAcademique().equals(anneeAcademique) 
                                  && i.getSemestre().equals(semestre))
                        .collect(Collectors.toList());
            }
            
            // Regrouper les notes par matière
            Map<String, MatiereNotesDTO> matiereMap = new HashMap<>();
            
            if (inscriptionsFiltrees != null && !inscriptionsFiltrees.isEmpty()) {
                for (Inscription inscription : inscriptionsFiltrees) {
                    if (inscription.getNotes() != null) {
                        for (Note note : inscription.getNotes()) {
                            String matiere = note.getLibelleMatiere();
                            
                            // Créer ou récupérer l'objet MatiereNotesDTO
                            MatiereNotesDTO matiereNotes = matiereMap.get(matiere);
                            if (matiereNotes == null) {
                                matiereNotes = new MatiereNotesDTO(matiere, inscription.getCodeUE());
                                // Ajouter les pourcentages depuis l'inscription
                                matiereNotes.setPourcentageCC(inscription.getPourcentageCC());
                                matiereNotes.setPourcentageSN(inscription.getPourcentageSN());
                                matiereNotes.setPourcentageTP(inscription.getPourcentageTP());
                                matiereMap.put(matiere, matiereNotes);
                            }
                            
                            // Ajouter la note selon son type
                            switch (note.getTypeEvaluation()) {
                                case CC:
                                    matiereNotes.setNoteCC(note.getValeur());
                                    break;
                                case SN:
                                    matiereNotes.setNoteSN(note.getValeur());
                                    break;
                                case TP:
                                    matiereNotes.setNoteTP(note.getValeur());
                                    break;
                                case RAT:
                                    matiereNotes.setNoteRAT(note.getValeur());
                                    break;
                            }
                        }
                    }
                }
            }
            
            // Calculer la moyenne pour chaque matière
            List<MatiereNotesDTO> matieres = new ArrayList<>(matiereMap.values());
            for (MatiereNotesDTO matiere : matieres) {
                List<Double> notes = new ArrayList<>();
                if (matiere.getNoteCC() != null) notes.add(matiere.getNoteCC());
                if (matiere.getNoteSN() != null) notes.add(matiere.getNoteSN());
                if (matiere.getNoteTP() != null) notes.add(matiere.getNoteTP());
                if (matiere.getNoteRAT() != null) notes.add(matiere.getNoteRAT());
                
                if (!notes.isEmpty()) {
                    double moyenne = notes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    matiere.setMoyenne(moyenne);
                }
            }
            
            // Calculer la moyenne générale
            double moyenneGenerale = matieres.stream()
                    .filter(m -> m.getMoyenne() != null)
                    .mapToDouble(MatiereNotesDTO::getMoyenne)
                    .average()
                    .orElse(0.0);
            parameters.put("moyenneGenerale", String.format("%.2f", moyenneGenerale));
            
            // Créer la source de données
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(matieres);
            
            // Charger le template
            InputStream reportStream = new ClassPathResource("reports/bulletin_notes.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // Remplir le rapport
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Exporter en PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du bulletin de notes", e);
        }
    }
}
