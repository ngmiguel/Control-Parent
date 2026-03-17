package org.example.backend.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.example.backend.dto.CoursDTO;
import org.example.backend.dto.EmploiDuTempsDTO;
import org.example.backend.entity.Classe;
import org.example.backend.entity.Cours;
import org.example.backend.entity.EmploiDuTemps;
import org.example.backend.repository.ClasseRepository;
import org.example.backend.repository.CoursRepository;
import org.example.backend.repository.EmploiDuTempsRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmploiDuTempsService {

    private final CoursRepository coursRepository;
    private final ClasseRepository classeRepository;
    private final EmploiDuTempsRepository emploiDuTempsRepository;

    public EmploiDuTempsService(CoursRepository coursRepository, ClasseRepository classeRepository,
                                EmploiDuTempsRepository emploiDuTempsRepository) {
        this.coursRepository = coursRepository;
        this.classeRepository = classeRepository;
        this.emploiDuTempsRepository = emploiDuTempsRepository;
    }

    /**
     * Récupère l'emploi du temps d'une classe
     */
    public EmploiDuTempsDTO getEmploiDuTempsParClasse(Long classeId) {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new IllegalArgumentException("Classe non trouvée"));
        
        EmploiDuTemps emploi = emploiDuTempsRepository.findAll().stream()
                .filter(e -> e.getClasse().getId().equals(classeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Emploi du temps non trouvé pour cette classe"));
        
        return new EmploiDuTempsDTO(
            emploi.getId(),
            emploi.getLibelle(),
            emploi.getUrl(),
            classe.getLibelle(),
            classe.getId()
        );
    }

    /**
     * Génère l'emploi du temps d'une classe en PDF (format grille avec créneaux horaires fixes)
     */
    public byte[] genererEmploiDuTempsPDF(Long classeId) {
        try {
            // Récupérer la classe
            Classe classe = classeRepository.findById(classeId)
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID: " + classeId));
            
            // Récupérer les cours de la classe
            List<Cours> cours = coursRepository.findByClasseIdOrderByJourSemaineAscHeureDebutAsc(classeId);
            
            // Map pour associer chaque matière à une couleur
            Map<String, String> matiereColors = new HashMap<>();
            String[] colors = {
                "5B9BD5", "ED7D31", "A5A5A5", "FFC000", "5470C6", 
                "91CC75", "FAC858", "EE6666", "73C0DE", "3BA272",
                "FC8452", "9A60B4", "EA7CCC"
            };
            int colorIndex = 0;
            
            // Créer une grille avec créneaux horaires fixes (8h-17h)
            String[] creneaux = {
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"
            };
            
            List<CoursDTO> coursDTO = new ArrayList<>();
            
            // Pour chaque créneau horaire
            for (String creneau : creneaux) {
                Map<String, String> joursData = new HashMap<>();
                Map<String, String> joursColors = new HashMap<>();
                
                // Extraire l'heure de début du créneau
                int heureCreneauDebut = Integer.parseInt(creneau.substring(0, 2));
                
                // PAUSE fixe entre 12h et 13h
                if (heureCreneauDebut == 12) {
                    joursData.put("Lundi", "PAUSE");
                    joursData.put("Mardi", "PAUSE");
                    joursData.put("Mercredi", "PAUSE");
                    joursData.put("Jeudi", "PAUSE");
                    joursData.put("Vendredi", "PAUSE");
                    joursData.put("Samedi", "PAUSE");
                    joursColors.put("Lundi", "D3D3D3");
                    joursColors.put("Mardi", "D3D3D3");
                    joursColors.put("Mercredi", "D3D3D3");
                    joursColors.put("Jeudi", "D3D3D3");
                    joursColors.put("Vendredi", "D3D3D3");
                    joursColors.put("Samedi", "D3D3D3");
                } else {
                    // Pour chaque cours, vérifier s'il correspond à ce créneau
                    for (Cours c : cours) {
                        // Ignorer les cours "PAUSE" dans la base de données
                        if (c.getMatiere().equals("PAUSE")) {
                            continue;
                        }
                        
                        int heureDebut = c.getHeureDebut().getHour();
                        int heureFin = c.getHeureFin().getHour();
                        
                        // Si le cours commence à cette heure (première cellule du cours)
                        if (heureDebut == heureCreneauDebut) {
                            joursData.put(c.getJourSemaine(), c.getMatiere());
                            
                            // Assigner une couleur à la matière si elle n'en a pas encore
                            String matiereName = c.getMatiere().split("\n")[0]; // Prendre juste le nom sans le prof
                            if (!matiereColors.containsKey(matiereName)) {
                                matiereColors.put(matiereName, colors[colorIndex % colors.length]);
                                colorIndex++;
                            }
                            joursColors.put(c.getJourSemaine(), matiereColors.get(matiereName));
                        }
                        // Si le cours a commencé avant et continue sur ce créneau (cellule de continuation)
                        else if (heureDebut < heureCreneauDebut && heureFin > heureCreneauDebut) {
                            // Marquer comme continuation
                            if (!joursData.containsKey(c.getJourSemaine())) {
                                joursData.put(c.getJourSemaine(), "↓");
                                String matiereName = c.getMatiere().split("\n")[0];
                                joursColors.put(c.getJourSemaine(), matiereColors.get(matiereName));
                            }
                        }
                    }
                    
                    // Remplir les créneaux vides avec "TPE" (sauf 12h-13h)
                    String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
                    for (String jour : jours) {
                        if (!joursData.containsKey(jour)) {
                            joursData.put(jour, "TPE");
                            joursColors.put(jour, "F5F5F5"); // Gris très clair pour TPE
                        }
                    }
                }
                
                coursDTO.add(new CoursDTO(
                    creneau,
                    joursData.get("Lundi"),
                    joursData.get("Mardi"),
                    joursData.get("Mercredi"),
                    joursData.get("Jeudi"),
                    joursData.get("Vendredi"),
                    joursData.get("Samedi"),
                    joursColors.get("Lundi"),
                    joursColors.get("Mardi"),
                    joursColors.get("Mercredi"),
                    joursColors.get("Jeudi"),
                    joursColors.get("Vendredi"),
                    joursColors.get("Samedi")
                ));
            }
            
            // Préparer les paramètres
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("classe", classe.getLibelle());
            parameters.put("filiere", classe.getFiliere());
            parameters.put("niveau", classe.getNiveau());
            parameters.put("dateGeneration", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Obtenir la semaine actuelle
            LocalDate today = LocalDate.now();
            LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate friday = monday.plusDays(4);
            
            String semaine = monday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                           " - " + 
                           friday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            parameters.put("semaine", semaine);
            
            // Créer la source de données
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(coursDTO);
            
            // Charger le template
            InputStream reportStream = new ClassPathResource("reports/emploi_du_temps.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            
            // Remplir le rapport
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Exporter en PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de l'emploi du temps: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération de l'emploi du temps", e);
        }
    }
}
