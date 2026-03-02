package org.example.backend.config;

import org.example.backend.entity.*;
import org.example.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ClasseRepository classeRepository;
    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final NoteRepository noteRepository;
    private final EmploiDuTempsRepository emploiDuTempsRepository;

    public DataInitializer(ClasseRepository classeRepository, EtudiantRepository etudiantRepository, 
                          InscriptionRepository inscriptionRepository, NoteRepository noteRepository, 
                          EmploiDuTempsRepository emploiDuTempsRepository) {
        this.classeRepository = classeRepository;
        this.etudiantRepository = etudiantRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.noteRepository = noteRepository;
        this.emploiDuTempsRepository = emploiDuTempsRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Initialisation des données - Institut Universitaire Saint Jean Cameroun ===");
        
        // Création des classes - Filières Ingénieur et Management
        Classe classeIng1 = createClasse("Ingénieur 1ère année", "Ingénieur", "Licence 1");
        Classe classeIng2 = createClasse("Ingénieur 2ème année", "Ingénieur", "Licence 2");
        Classe classeIng3 = createClasse("Ingénieur 3ème année", "Ingénieur", "Licence 3");
        Classe classeIng4 = createClasse("Ingénieur 4ème année", "Ingénieur", "Master 1");
        Classe classeIng5 = createClasse("Ingénieur 5ème année", "Ingénieur", "Master 2");
        
        Classe classeMgt1 = createClasse("Management 1ère année", "Management", "Licence 1");
        Classe classeMgt2 = createClasse("Management 2ème année", "Management", "Licence 2");
        Classe classeMgt3 = createClasse("Management 3ème année", "Management", "Licence 3");
        
        System.out.println("Classes créées: " + classeRepository.count());
        
        // Création des étudiants avec noms camerounais
        Etudiant etudiant1 = createEtudiant(
            "IUSJ2024001", "NGONO", "Paul Brice", LocalDate.of(2004, 3, 15),
            "NGONO Martin", "EBODE Marie Claire", 
            "670123456/690234567", "675345678", classeIng1
        );
        
        Etudiant etudiant2 = createEtudiant(
            "IUSJ2024002", "MBALLA", "Christelle Vanessa", LocalDate.of(2003, 7, 22),
            "MBALLA Jean Pierre", "ATANGANA Berthe", 
            "670456789", "675567890/680678901", classeMgt1
        );
        
        Etudiant etudiant3 = createEtudiant(
            "IUSJ2024003", "FOTSO", "Rodrigue", LocalDate.of(2002, 11, 8),
            "FOTSO Emmanuel", "TCHUENTE Sylvie", 
            "(237)670789012", "675890123", classeIng3
        );
        
        Etudiant etudiant4 = createEtudiant(
            "IUSJ2024004", "ESSOMBA", "Grace Nadège", LocalDate.of(2003, 5, 18),
            "ESSOMBA François", "NKOLO Jeanne", 
            "670901234/690012345", "675123456", classeMgt2
        );
        
        Etudiant etudiant5 = createEtudiant(
            "IUSJ2024005", "KAMGA", "Alain Junior", LocalDate.of(2001, 9, 30),
            "KAMGA Robert", "NANA Pauline", 
            "670234567", "675456789/(237)680567890", classeIng4
        );
        
        Etudiant etudiant6 = createEtudiant(
            "IUSJ2024006", "ONDOA", "Stéphane", LocalDate.of(2000, 12, 5),
            "ONDOA Pierre", "MANGA Hélène", 
            "670345678/690456789/(237)680678901", "675789012", classeIng5
        );
        
        Etudiant etudiant7 = createEtudiant(
            "IUSJ2024007", "ABENA", "Sandrine Laure", LocalDate.of(2004, 2, 14),
            "ABENA Joseph", "MVONDO Christine", 
            "670567890", "675890123/680901234", classeMgt3
        );
        
        Etudiant etudiant8 = createEtudiant(
            "IUSJ2024008", "TCHUENTE", "Boris Kevin", LocalDate.of(2003, 6, 20),
            "TCHUENTE André", "NKOLO Patience", 
            "670678901/690789012", "675901234", classeIng2
        );
        
        // Étudiants avec même parent (frères et sœurs)
        Etudiant etudiant9 = createEtudiant(
            "IUSJ2024009", "NGONO", "Diane Flore", LocalDate.of(2005, 8, 10),
            "NGONO Martin", "EBODE Marie Claire", 
            "670123456/690234567", "675345678", classeMgt1
        );
        
        Etudiant etudiant10 = createEtudiant(
            "IUSJ2024010", "BIYA", "Hermann", LocalDate.of(2002, 4, 25),
            "BIYA Samuel", "FOUDA Angeline", 
            "670890123", "675012345/(237)680123456", classeIng3
        );
        
        System.out.println("Étudiants créés: " + etudiantRepository.count());
        
        // Création des inscriptions et notes pour chaque étudiant
        createInscriptionsAndNotesIngenieur(etudiant1, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant2, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant3, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant4, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant5, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant6, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant7, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant8, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant9, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant10, "2024-2025", "Semestre 1");
        
        System.out.println("Inscriptions créées: " + inscriptionRepository.count());
        System.out.println("Notes créées: " + noteRepository.count());
        
        // Création des emplois du temps
        createEmploiDuTemps(classeIng1, "Emploi du temps Ingénieur 1ère année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/ing1-s1.pdf");
        createEmploiDuTemps(classeIng2, "Emploi du temps Ingénieur 2ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/ing2-s1.pdf");
        createEmploiDuTemps(classeIng3, "Emploi du temps Ingénieur 3ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/ing3-s1.pdf");
        createEmploiDuTemps(classeIng4, "Emploi du temps Ingénieur 4ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/ing4-s1.pdf");
        createEmploiDuTemps(classeIng5, "Emploi du temps Ingénieur 5ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/ing5-s1.pdf");
        createEmploiDuTemps(classeMgt1, "Emploi du temps Management 1ère année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/mgt1-s1.pdf");
        createEmploiDuTemps(classeMgt2, "Emploi du temps Management 2ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/mgt2-s1.pdf");
        createEmploiDuTemps(classeMgt3, "Emploi du temps Management 3ème année - Semestre 1", 
            "https://iusj-cameroun.edu/emploi-du-temps/mgt3-s1.pdf");
        
        System.out.println("Emplois du temps créés: " + emploiDuTempsRepository.count());
        System.out.println("=== Initialisation des données terminée avec succès! ===");
    }
    
    private Classe createClasse(String libelle, String filiere, String niveau) {
        Classe classe = new Classe();
        classe.setLibelle(libelle);
        classe.setFiliere(filiere);
        classe.setNiveau(niveau);
        return classeRepository.save(classe);
    }
    
    private Etudiant createEtudiant(String matricule, String nom, String prenom, 
                                    LocalDate dateNaissance, String nomPere, String nomMere,
                                    String telPere, String telMere, Classe classe) {
        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(matricule);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setDateNaissance(dateNaissance);
        etudiant.setNomPere(nomPere);
        etudiant.setNomMere(nomMere);
        etudiant.setTelephonePere(telPere);
        etudiant.setTelephoneMere(telMere);
        etudiant.setClasse(classe);
        return etudiantRepository.save(etudiant);
    }
    
    private void createInscriptionsAndNotesIngenieur(Etudiant etudiant, String anneeAcademique, String semestre) {
        // Inscription en Mathématiques
        Inscription inscMath = createInscription(etudiant, "UE-ING-MATH-001", anneeAcademique, semestre);
        createNote(inscMath, Note.TypeEvaluation.CC, 14.5, "Mathématiques");
        createNote(inscMath, Note.TypeEvaluation.SN, 16.0, "Mathématiques");
        createNote(inscMath, Note.TypeEvaluation.TP, 15.5, "Mathématiques");
        
        // Inscription en Programmation
        Inscription inscProg = createInscription(etudiant, "UE-ING-PROG-001", anneeAcademique, semestre);
        createNote(inscProg, Note.TypeEvaluation.CC, 15.0, "Programmation");
        createNote(inscProg, Note.TypeEvaluation.TP, 17.0, "Programmation");
        createNote(inscProg, Note.TypeEvaluation.SN, 16.5, "Programmation");
        
        // Inscription en Physique
        Inscription inscPhysique = createInscription(etudiant, "UE-ING-PHYS-001", anneeAcademique, semestre);
        createNote(inscPhysique, Note.TypeEvaluation.CC, 13.0, "Physique");
        createNote(inscPhysique, Note.TypeEvaluation.TP, 14.5, "Physique");
        createNote(inscPhysique, Note.TypeEvaluation.SN, 13.5, "Physique");
        
        // Inscription en Électronique
        Inscription inscElec = createInscription(etudiant, "UE-ING-ELEC-001", anneeAcademique, semestre);
        createNote(inscElec, Note.TypeEvaluation.CC, 12.5, "Électronique");
        createNote(inscElec, Note.TypeEvaluation.TP, 14.0, "Électronique");
        createNote(inscElec, Note.TypeEvaluation.SN, 13.0, "Électronique");
        
        // Inscription en Algorithmique
        Inscription inscAlgo = createInscription(etudiant, "UE-ING-ALGO-001", anneeAcademique, semestre);
        createNote(inscAlgo, Note.TypeEvaluation.CC, 16.0, "Algorithmique");
        createNote(inscAlgo, Note.TypeEvaluation.TP, 17.5, "Algorithmique");
        createNote(inscAlgo, Note.TypeEvaluation.SN, 16.5, "Algorithmique");
        
        // Inscription en Anglais Technique
        Inscription inscAnglais = createInscription(etudiant, "UE-ING-ANG-001", anneeAcademique, semestre);
        createNote(inscAnglais, Note.TypeEvaluation.CC, 14.0, "Anglais Technique");
        createNote(inscAnglais, Note.TypeEvaluation.SN, 15.0, "Anglais Technique");
    }
    
    private void createInscriptionsAndNotesManagement(Etudiant etudiant, String anneeAcademique, String semestre) {
        // Inscription en Comptabilité
        Inscription inscCompta = createInscription(etudiant, "UE-MGT-COMPTA-001", anneeAcademique, semestre);
        createNote(inscCompta, Note.TypeEvaluation.CC, 13.5, "Comptabilité Générale");
        createNote(inscCompta, Note.TypeEvaluation.SN, 14.0, "Comptabilité Générale");
        createNote(inscCompta, Note.TypeEvaluation.TP, 15.0, "Comptabilité Générale");
        
        // Inscription en Marketing
        Inscription inscMarketing = createInscription(etudiant, "UE-MGT-MKT-001", anneeAcademique, semestre);
        createNote(inscMarketing, Note.TypeEvaluation.CC, 15.5, "Marketing");
        createNote(inscMarketing, Note.TypeEvaluation.SN, 16.0, "Marketing");
        
        // Inscription en Gestion des Ressources Humaines
        Inscription inscGRH = createInscription(etudiant, "UE-MGT-GRH-001", anneeAcademique, semestre);
        createNote(inscGRH, Note.TypeEvaluation.CC, 14.0, "Gestion des Ressources Humaines");
        createNote(inscGRH, Note.TypeEvaluation.SN, 15.5, "Gestion des Ressources Humaines");
        
        // Inscription en Économie
        Inscription inscEco = createInscription(etudiant, "UE-MGT-ECO-001", anneeAcademique, semestre);
        createNote(inscEco, Note.TypeEvaluation.CC, 12.5, "Économie");
        createNote(inscEco, Note.TypeEvaluation.SN, 13.0, "Économie");
        
        // Inscription en Droit des Affaires
        Inscription inscDroit = createInscription(etudiant, "UE-MGT-DROIT-001", anneeAcademique, semestre);
        createNote(inscDroit, Note.TypeEvaluation.CC, 13.0, "Droit des Affaires");
        createNote(inscDroit, Note.TypeEvaluation.SN, 14.5, "Droit des Affaires");
        
        // Inscription en Statistiques
        Inscription inscStat = createInscription(etudiant, "UE-MGT-STAT-001", anneeAcademique, semestre);
        createNote(inscStat, Note.TypeEvaluation.CC, 14.5, "Statistiques");
        createNote(inscStat, Note.TypeEvaluation.TP, 15.0, "Statistiques");
        createNote(inscStat, Note.TypeEvaluation.SN, 14.0, "Statistiques");
    }
    
    private Inscription createInscription(Etudiant etudiant, String codeUE, 
                                         String anneeAcademique, String semestre) {
        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant);
        inscription.setCodeUE(codeUE);
        inscription.setAnneeAcademique(anneeAcademique);
        inscription.setSemestre(semestre);
        return inscriptionRepository.save(inscription);
    }
    
    private void createNote(Inscription inscription, Note.TypeEvaluation type, 
                           Double valeur, String libelleMatiere) {
        Note note = new Note();
        note.setInscription(inscription);
        note.setTypeEvaluation(type);
        note.setValeur(valeur);
        note.setLibelleMatiere(libelleMatiere);
        noteRepository.save(note);
    }
    
    private void createEmploiDuTemps(Classe classe, String libelle, String url) {
        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.setClasse(classe);
        emploi.setLibelle(libelle);
        emploi.setUrl(url);
        emploiDuTempsRepository.save(emploi);
    }
}
