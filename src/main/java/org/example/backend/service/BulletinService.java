package org.example.backend.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.example.backend.dto.NoteDTO;
import org.example.backend.entity.Etudiant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BulletinService {

    private final EtudiantService etudiantService;

    public BulletinService(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }


    /**
     * Génère le bulletin de notes d'un étudiant pour une période spécifique
     * Si semestre = "Annuel", génère un PDF avec 2 pages (Semestre 1 + Semestre 2)
     */
    public byte[] genererBulletinPDFParPeriode(String matricule, String anneeAcademique, String semestre) {
        try {
            System.out.println("=== Début génération bulletin pour matricule: " + matricule + " (" + anneeAcademique + " - " + semestre + ") ===");
            
            // Si "Annuel", générer un PDF avec 2 pages (S1 + S2)
            if ("Annuel".equalsIgnoreCase(semestre)) {
                return genererBulletinAnnuel(matricule, anneeAcademique);
            }
            
            // Sinon, générer le bulletin pour un semestre spécifique
            return genererBulletinSemestre(matricule, anneeAcademique, semestre);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du bulletin: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du bulletin", e);
        }
    }
    
    /**
     * Génère un bulletin annuel avec 2 pages (Semestre 1 + Semestre 2)
     */
    private byte[] genererBulletinAnnuel(String matricule, String anneeAcademique) {
        try {
            System.out.println("=== Génération bulletin ANNUEL ===");
            
            // Générer le bulletin Semestre 1
            byte[] pdfS1 = genererBulletinSemestre(matricule, anneeAcademique, "Semestre 1");
            System.out.println("✅ Bulletin Semestre 1 généré: " + pdfS1.length + " bytes");
            
            // Générer le bulletin Semestre 2
            byte[] pdfS2 = genererBulletinSemestre(matricule, anneeAcademique, "Semestre 2");
            System.out.println("✅ Bulletin Semestre 2 généré: " + pdfS2.length + " bytes");
            
            // Fusionner les 2 PDF en un seul
            byte[] pdfFusionne = fusionnerPDF(pdfS1, pdfS2);
            System.out.println("✅ PDF fusionné: " + pdfFusionne.length + " bytes");
            System.out.println("=== Fin génération bulletin ANNUEL ===");
            
            return pdfFusionne;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du bulletin annuel: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du bulletin annuel", e);
        }
    }
    
    /**
     * Fusionne deux PDF en un seul en utilisant Apache PDFBox
     */
    private byte[] fusionnerPDF(byte[] pdf1, byte[] pdf2) {
        try {
            // Charger les deux PDF
            PDDocument doc1 = PDDocument.load(pdf1);
            PDDocument doc2 = PDDocument.load(pdf2);
            
            // Créer un nouveau document pour la fusion
            PDDocument mergedDoc = new PDDocument();
            
            // Ajouter toutes les pages du premier PDF
            for (int i = 0; i < doc1.getNumberOfPages(); i++) {
                mergedDoc.addPage(doc1.getPage(i));
            }
            
            // Ajouter toutes les pages du deuxième PDF
            for (int i = 0; i < doc2.getNumberOfPages(); i++) {
                mergedDoc.addPage(doc2.getPage(i));
            }
            
            // Sauvegarder le document fusionné dans un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mergedDoc.save(outputStream);
            
            // Fermer les documents
            doc1.close();
            doc2.close();
            mergedDoc.close();
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la fusion des PDF", e);
        }
    }
    
    /**
     * Génère le bulletin pour un semestre spécifique
     */
    private byte[] genererBulletinSemestre(String matricule, String anneeAcademique, String semestre) {
        try {
            System.out.println("=== Génération bulletin pour " + semestre + " ===");
            
            // Récupérer l'étudiant
            Etudiant etudiant = etudiantService.getEtudiantByMatricule(matricule);
            System.out.println("✅ Étudiant trouvé: " + etudiant.getNom() + " " + etudiant.getPrenom());
            
            // Récupérer les notes pour la période spécifique
            List<NoteDTO> notes = etudiantService.getNotesEtudiantParPeriode(matricule, anneeAcademique, semestre);
            System.out.println("✅ Nombre de notes trouvées: " + notes.size());
            
            if (notes.isEmpty()) {
                throw new RuntimeException("Aucune note trouvée pour l'étudiant " + matricule + " pour la période " + anneeAcademique + " - " + semestre);
            }
            
            // Grouper les notes par matière et calculer les moyennes
            Map<String, List<NoteDTO>> notesParMatiere = notes.stream()
                    .collect(Collectors.groupingBy(NoteDTO::getLibelleMatiere));
            
            List<Map<String, Object>> bulletinData = new ArrayList<>();
            double sommeGenerale = 0;
            int nombreMatieres = 0;
            
            for (Map.Entry<String, List<NoteDTO>> entry : notesParMatiere.entrySet()) {
                String matiere = entry.getKey();
                List<NoteDTO> notesMatiere = entry.getValue();
                
                Map<String, Object> ligne = new HashMap<>();
                ligne.put("matiere", matiere);
                ligne.put("codeUE", notesMatiere.get(0).getCodeUE());
                
                // Récupérer les pourcentages depuis la première note
                Integer pourcentageCC = notesMatiere.get(0).getPourcentageCC();
                Integer pourcentageSN = notesMatiere.get(0).getPourcentageSN();
                Integer pourcentageTP = notesMatiere.get(0).getPourcentageTP();
                
                // Récupérer les notes par type
                Double noteCC = notesMatiere.stream()
                        .filter(n -> "CC".equals(n.getTypeEvaluation()))
                        .map(NoteDTO::getValeur)
                        .findFirst().orElse(null);
                
                Double noteSN = notesMatiere.stream()
                        .filter(n -> "SN".equals(n.getTypeEvaluation()))
                        .map(NoteDTO::getValeur)
                        .findFirst().orElse(null);
                
                Double noteTP = notesMatiere.stream()
                        .filter(n -> "TP".equals(n.getTypeEvaluation()))
                        .map(NoteDTO::getValeur)
                        .findFirst().orElse(null);
                
                // Afficher les pourcentages et notes
                ligne.put("pourcentageCC", pourcentageCC + "%");
                ligne.put("noteCC", noteCC != null ? String.format("%.2f", noteCC) : "-");
                ligne.put("pourcentageSN", pourcentageSN + "%");
                ligne.put("noteSN", noteSN != null ? String.format("%.2f", noteSN) : "-");
                ligne.put("pourcentageTP", pourcentageTP + "%");
                ligne.put("noteTP", noteTP != null ? String.format("%.2f", noteTP) : "-");
                
                // Calculer la moyenne PONDÉRÉE de la matière
                double moyennePonderee = 0;
                if (noteCC != null && noteSN != null && noteTP != null) {
                    moyennePonderee = (noteCC * pourcentageCC / 100.0) + 
                                     (noteSN * pourcentageSN / 100.0) + 
                                     (noteTP * pourcentageTP / 100.0);
                } else if (noteCC != null && noteSN != null) {
                    // Si pas de TP, recalculer les pourcentages
                    double totalPourcentage = pourcentageCC + pourcentageSN;
                    moyennePonderee = (noteCC * pourcentageCC / totalPourcentage) + 
                                     (noteSN * pourcentageSN / totalPourcentage);
                } else {
                    // Fallback: moyenne simple
                    double somme = 0;
                    int count = 0;
                    if (noteCC != null) { somme += noteCC; count++; }
                    if (noteSN != null) { somme += noteSN; count++; }
                    if (noteTP != null) { somme += noteTP; count++; }
                    moyennePonderee = count > 0 ? somme / count : 0;
                }
                
                ligne.put("moyenne", String.format("%.2f", moyennePonderee));
                
                bulletinData.add(ligne);
                sommeGenerale += moyennePonderee;
                nombreMatieres++;
            }
            
            // Calculer la moyenne générale
            double moyenneGenerale = nombreMatieres > 0 ? sommeGenerale / nombreMatieres : 0;
            System.out.println("✅ Nombre de matières: " + nombreMatieres);
            System.out.println("✅ Moyenne générale calculée: " + moyenneGenerale);
            
            // Préparer les paramètres
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("matricule", etudiant.getMatricule());
            parameters.put("nom", etudiant.getNom());
            parameters.put("prenom", etudiant.getPrenom());
            parameters.put("dateNaissance", etudiant.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            parameters.put("classe", etudiant.getClasse().getLibelle());
            parameters.put("filiere", etudiant.getClasse().getFiliere());
            parameters.put("niveau", etudiant.getClasse().getNiveau());
            parameters.put("anneeAcademique", anneeAcademique);
            parameters.put("semestre", semestre);
            parameters.put("moyenneGenerale", String.format("%.2f", moyenneGenerale));
            parameters.put("dateGeneration", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Appréciation générale
            String appreciationGenerale;
            if (moyenneGenerale >= 16) appreciationGenerale = "Excellent travail";
            else if (moyenneGenerale >= 14) appreciationGenerale = "Bon travail";
            else if (moyenneGenerale >= 12) appreciationGenerale = "Travail satisfaisant";
            else if (moyenneGenerale >= 10) appreciationGenerale = "Peut mieux faire";
            else appreciationGenerale = "Travail insuffisant";
            
            parameters.put("appreciationGenerale", appreciationGenerale);
            
            // Créer la source de données
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bulletinData);
            
            // Charger le template
            InputStream reportStream = new ClassPathResource("reports/bulletin_notes.jrxml").getInputStream();
            System.out.println("✅ Template chargé");
            
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            System.out.println("✅ Template compilé");
            
            // Remplir le rapport
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            System.out.println("✅ Rapport rempli avec " + jasperPrint.getPages().size() + " page(s)");
            
            // Exporter en PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            System.out.println("✅ PDF généré: " + pdfBytes.length + " bytes");
            
            return pdfBytes;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du bulletin: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du bulletin", e);
        }
    }
}
