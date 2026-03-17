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
    private final CoursRepository coursRepository;

    public DataInitializer(ClasseRepository classeRepository, EtudiantRepository etudiantRepository, 
                          InscriptionRepository inscriptionRepository, NoteRepository noteRepository, 
                          EmploiDuTempsRepository emploiDuTempsRepository, CoursRepository coursRepository) {
        this.classeRepository = classeRepository;
        this.etudiantRepository = etudiantRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.noteRepository = noteRepository;
        this.emploiDuTempsRepository = emploiDuTempsRepository;
        this.coursRepository = coursRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Initialisation des donnees - Institut Universitaire Saint Jean Cameroun ===");
        
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
        
        // Création des étudiants avec matricules selon le format: annéeAcadémique-filière-numéro
        // Format: 2425i752 (année 2024-2025, Ingénieur, numéro 752)
        // Format: 2425m984 (année 2024-2025, Management, numéro 984)
        
        // PARENT TEST avec 3 ENFANTS - Numéro: 696091296
        Etudiant etudiant1 = createEtudiant(
            "2425i001", "NGONO", "Paul Brice", LocalDate.of(2004, 3, 15),
            "NGONO Martin", "EBODE Marie Claire", 
            "696091296", "655029771", classeIng1
        );
        
        Etudiant etudiant2 = createEtudiant(
            "2425m001", "NGONO", "Sophie Diane", LocalDate.of(2005, 7, 22),
            "NGONO Martin", "EBODE Marie Claire", 
            "696091296", "655029771", classeMgt1
        );
        
        Etudiant etudiant3 = createEtudiant(
            "2324i152", "NGONO", "Junior Patrick", LocalDate.of(2006, 11, 8),
            "NGONO Martin", "EBODE Marie Claire", 
            "696091296", "655029771", classeIng2
        );
        
        // Autres étudiants
        Etudiant etudiant4 = createEtudiant(
            "2223i152", "FOTSO", "Rodrigue", LocalDate.of(2002, 11, 8),
            "FOTSO Emmanuel", "TCHUENTE Sylvie", 
            "(237)655029771", "675890123", classeIng3
        );
        
        Etudiant etudiant5 = createEtudiant(
            "2324m045", "ESSOMBA", "Grace Nadège", LocalDate.of(2003, 5, 18),
            "ESSOMBA François", "NKOLO Jeanne", 
            "670901234/690012345", "675123456", classeMgt2
        );
        
        Etudiant etudiant6 = createEtudiant(
            "2122i378", "KAMGA", "Alain Junior", LocalDate.of(2001, 9, 30),
            "KAMGA Robert", "NANA Pauline", 
            "670234567", "675456789/(237)680567890", classeIng4
        );
        
        Etudiant etudiant7 = createEtudiant(
            "2021i512", "ONDOA", "Stéphane", LocalDate.of(2000, 12, 5),
            "ONDOA Pierre", "MANGA Hélène", 
            "670345678/690456789/(237)680678901", "675789012", classeIng5
        );
        
        Etudiant etudiant8 = createEtudiant(
            "2223m089", "ABENA", "Sandrine Laure", LocalDate.of(2004, 2, 14),
            "ABENA Joseph", "MVONDO Christine", 
            "670567890", "675890123/680901234", classeMgt3
        );
        
        Etudiant etudiant9 = createEtudiant(
            "2324i234", "TCHUENTE", "Boris Kevin", LocalDate.of(2003, 6, 20),
            "TCHUENTE André", "NKOLO Patience", 
            "670678901/690789012", "675901234", classeIng2
        );
        
        Etudiant etudiant10 = createEtudiant(
            "2223i456", "BIYA", "Hermann", LocalDate.of(2002, 4, 25),
            "BIYA Samuel", "FOUDA Angeline", 
            "670890123", "675012345/(237)680123456", classeIng3
        );
        
        System.out.println("Étudiants créés: " + etudiantRepository.count());
        
        // Création des inscriptions et notes pour chaque étudiant - PLUSIEURS ANNÉES ET SEMESTRES
        // Année 2024-2025 - Semestre 1 et 2 (notes différentes pour chaque semestre)
        createInscriptionsAndNotesIngenieur(etudiant1, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant1, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesManagement(etudiant2, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant2, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant3, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant3, "2024-2025", "Semestre 2");
        
        // Année 2023-2024 (pour les 3 enfants du parent test)
        createInscriptionsAndNotesIngenieur(etudiant1, "2023-2024", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant1, "2023-2024", "Semestre 2");
        createInscriptionsAndNotesManagement(etudiant2, "2023-2024", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant2, "2023-2024", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant3, "2023-2024", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant3, "2023-2024", "Semestre 2");
        
        // Autres étudiants - année courante seulement
        createInscriptionsAndNotesIngenieur(etudiant4, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant4, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesManagement(etudiant5, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant5, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant6, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant6, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant7, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant7, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesManagement(etudiant8, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesManagement(etudiant8, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant9, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant9, "2024-2025", "Semestre 2");
        createInscriptionsAndNotesIngenieur(etudiant10, "2024-2025", "Semestre 1");
        createInscriptionsAndNotesIngenieur(etudiant10, "2024-2025", "Semestre 2");
        
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
        
        System.out.println("Emplois du temps crees: " + emploiDuTempsRepository.count());
        
        // Création des cours pour les emplois du temps (avec PAUSE 12h-13h)
        createCoursIngenieur1(classeIng1);
        createCoursManagement1(classeMgt1);
        createCoursIngenieur2(classeIng2);
        
        System.out.println("Cours créés: " + coursRepository.count());
        System.out.println("=== Initialisation des donnees terminee avec succes! ===");
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
        // Rang aléatoire entre 1 et 50 pour la démonstration
        etudiant.setRang((int)(Math.random() * 50) + 1);
        return etudiantRepository.save(etudiant);
    }
    
    private void createInscriptionsAndNotesIngenieur(Etudiant etudiant, String anneeAcademique, String semestre) {
        // Notes différentes selon le semestre pour tester
        double facteur = semestre.equals("Semestre 2") ? 1.1 : 1.0; // S2 légèrement meilleures
        
        // Inscription en Mathématiques
        Inscription inscMath = createInscription(etudiant, "UE-ING-MATH-001", anneeAcademique, semestre);
        createNote(inscMath, Note.TypeEvaluation.CC, 14.5 * facteur, "Mathématiques");
        createNote(inscMath, Note.TypeEvaluation.SN, 16.0 * facteur, "Mathématiques");
        createNote(inscMath, Note.TypeEvaluation.TP, 15.5 * facteur, "Mathématiques");
        
        // Inscription en Programmation
        Inscription inscProg = createInscription(etudiant, "UE-ING-PROG-001", anneeAcademique, semestre);
        createNote(inscProg, Note.TypeEvaluation.CC, 15.0 * facteur, "Programmation");
        createNote(inscProg, Note.TypeEvaluation.TP, 17.0 * facteur, "Programmation");
        createNote(inscProg, Note.TypeEvaluation.SN, 16.5 * facteur, "Programmation");
        
        // Inscription en Physique
        Inscription inscPhysique = createInscription(etudiant, "UE-ING-PHYS-001", anneeAcademique, semestre);
        createNote(inscPhysique, Note.TypeEvaluation.CC, 13.0 * facteur, "Physique");
        createNote(inscPhysique, Note.TypeEvaluation.TP, 14.5 * facteur, "Physique");
        createNote(inscPhysique, Note.TypeEvaluation.SN, 13.5 * facteur, "Physique");
        
        // Inscription en Électronique
        Inscription inscElec = createInscription(etudiant, "UE-ING-ELEC-001", anneeAcademique, semestre);
        createNote(inscElec, Note.TypeEvaluation.CC, 12.5 * facteur, "Électronique");
        createNote(inscElec, Note.TypeEvaluation.TP, 14.0 * facteur, "Électronique");
        createNote(inscElec, Note.TypeEvaluation.SN, 13.0 * facteur, "Électronique");
        
        // Inscription en Algorithmique
        Inscription inscAlgo = createInscription(etudiant, "UE-ING-ALGO-001", anneeAcademique, semestre);
        createNote(inscAlgo, Note.TypeEvaluation.CC, 16.0 * facteur, "Algorithmique");
        createNote(inscAlgo, Note.TypeEvaluation.TP, 17.5 * facteur, "Algorithmique");
        createNote(inscAlgo, Note.TypeEvaluation.SN, 16.5 * facteur, "Algorithmique");
        
        // Inscription en Anglais Technique
        Inscription inscAnglais = createInscription(etudiant, "UE-ING-ANG-001", anneeAcademique, semestre);
        createNote(inscAnglais, Note.TypeEvaluation.CC, 14.0 * facteur, "Anglais Technique");
        createNote(inscAnglais, Note.TypeEvaluation.SN, 15.0 * facteur, "Anglais Technique");
    }
    
    private void createInscriptionsAndNotesManagement(Etudiant etudiant, String anneeAcademique, String semestre) {
        // Notes différentes selon le semestre pour tester
        double facteur = semestre.equals("Semestre 2") ? 1.1 : 1.0; // S2 légèrement meilleures
        
        // Inscription en Comptabilité
        Inscription inscCompta = createInscription(etudiant, "UE-MGT-COMPTA-001", anneeAcademique, semestre);
        createNote(inscCompta, Note.TypeEvaluation.CC, 13.5 * facteur, "Comptabilité Générale");
        createNote(inscCompta, Note.TypeEvaluation.SN, 14.0 * facteur, "Comptabilité Générale");
        createNote(inscCompta, Note.TypeEvaluation.TP, 15.0 * facteur, "Comptabilité Générale");
        
        // Inscription en Marketing
        Inscription inscMarketing = createInscription(etudiant, "UE-MGT-MKT-001", anneeAcademique, semestre);
        createNote(inscMarketing, Note.TypeEvaluation.CC, 15.5 * facteur, "Marketing");
        createNote(inscMarketing, Note.TypeEvaluation.SN, 16.0 * facteur, "Marketing");
        
        // Inscription en Gestion des Ressources Humaines
        Inscription inscGRH = createInscription(etudiant, "UE-MGT-GRH-001", anneeAcademique, semestre);
        createNote(inscGRH, Note.TypeEvaluation.CC, 14.0 * facteur, "Gestion des Ressources Humaines");
        createNote(inscGRH, Note.TypeEvaluation.SN, 15.5 * facteur, "Gestion des Ressources Humaines");
        
        // Inscription en Économie
        Inscription inscEco = createInscription(etudiant, "UE-MGT-ECO-001", anneeAcademique, semestre);
        createNote(inscEco, Note.TypeEvaluation.CC, 12.5 * facteur, "Économie");
        createNote(inscEco, Note.TypeEvaluation.SN, 13.0 * facteur, "Économie");
        
        // Inscription en Droit des Affaires
        Inscription inscDroit = createInscription(etudiant, "UE-MGT-DROIT-001", anneeAcademique, semestre);
        createNote(inscDroit, Note.TypeEvaluation.CC, 13.0 * facteur, "Droit des Affaires");
        createNote(inscDroit, Note.TypeEvaluation.SN, 14.5 * facteur, "Droit des Affaires");
        
        // Inscription en Statistiques
        Inscription inscStat = createInscription(etudiant, "UE-MGT-STAT-001", anneeAcademique, semestre);
        createNote(inscStat, Note.TypeEvaluation.CC, 14.5 * facteur, "Statistiques");
        createNote(inscStat, Note.TypeEvaluation.TP, 15.0 * facteur, "Statistiques");
        createNote(inscStat, Note.TypeEvaluation.SN, 14.0 * facteur, "Statistiques");
    }
    
    private Inscription createInscription(Etudiant etudiant, String codeUE, 
                                         String anneeAcademique, String semestre) {
        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant);
        inscription.setCodeUE(codeUE);
        inscription.setAnneeAcademique(anneeAcademique);
        inscription.setSemestre(semestre);
        // Les pourcentages sont initialisés par le constructeur par défaut (30, 50, 20)
        System.out.println("✅ Inscription créée avec pourcentages: CC=" + inscription.getPourcentageCC() + 
                          "%, SN=" + inscription.getPourcentageSN() + "%, TP=" + inscription.getPourcentageTP() + "%");
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
    
    private void createCoursIngenieur(Classe classe) {
        // Méthode obsolète - utiliser createCoursIngenieur1, createCoursIngenieur2, etc.
    }
    
    private void createCoursManagement(Classe classe) {
        // Méthode obsolète - utiliser createCoursManagement1, createCoursManagement2, etc.
    }
    
    // INGÉNIEUR 1ÈRE ANNÉE
    private void createCoursIngenieur1(Classe classe) {
        // Lundi
        createCours(classe, "Lundi", "08:00", "09:00", "Machine Learning\nABDOURAMAN", "Prof. ABDOURAMAN", "Salle A101");
        createCours(classe, "Lundi", "09:00", "10:00", "Machine Learning\nABDOURAMAN", "Prof. ABDOURAMAN", "Salle A101");
        createCours(classe, "Lundi", "10:00", "11:00", "Machine Learning\nABDOURAMAN", "Prof. ABDOURAMAN", "Salle A101");
        createCours(classe, "Lundi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Lundi", "13:00", "14:00", "Projet Tutoré\nMONDOU", "Prof. MONDOU", "Lab Info 1");
        createCours(classe, "Lundi", "14:00", "15:00", "Projet Tutoré\nMONDOU", "Prof. MONDOU", "Lab Info 1");
        createCours(classe, "Lundi", "15:00", "16:00", "Travail personnel de l'étudiant", "", "");
        createCours(classe, "Lundi", "16:00", "17:00", "Travail personnel de l'étudiant", "", "");
        
        // Mardi
        createCours(classe, "Mardi", "08:00", "09:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Salle B203");
        createCours(classe, "Mardi", "09:00", "10:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Salle B203");
        createCours(classe, "Mardi", "10:00", "11:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Salle B203");
        createCours(classe, "Mardi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Mardi", "13:00", "14:00", "Sagesse et Science\nFr Georges", "Fr. GEORGES", "Salle A102");
        createCours(classe, "Mardi", "14:00", "15:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Lab Info 2");
        createCours(classe, "Mardi", "15:00", "16:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Lab Info 2");
        createCours(classe, "Mardi", "16:00", "17:00", "Cloud, Virtualisation et datacenter\nTAMKO", "Prof. TAMKO", "Lab Info 2");
        
        // Mercredi
        createCours(classe, "Mercredi", "08:00", "09:00", "Compilation\nKOUAMOU", "Prof. KOUAMOU", "Salle C105");
        createCours(classe, "Mercredi", "09:00", "10:00", "Compilation\nKOUAMOU", "Prof. KOUAMOU", "Salle C105");
        createCours(classe, "Mercredi", "10:00", "11:00", "Compilation\nKOUAMOU", "Prof. KOUAMOU", "Salle C105");
        createCours(classe, "Mercredi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Mercredi", "13:00", "14:00", "PAUSE", "", "");
        createCours(classe, "Mercredi", "14:00", "15:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        createCours(classe, "Mercredi", "15:00", "16:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        createCours(classe, "Mercredi", "16:00", "17:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        
        // Jeudi
        createCours(classe, "Jeudi", "08:00", "09:00", "Entrepreneuriat 1\nMONDO", "Prof. MONDO", "Salle D201");
        createCours(classe, "Jeudi", "09:00", "10:00", "Entrepreneuriat 1\nMONDO", "Prof. MONDO", "Salle D201");
        createCours(classe, "Jeudi", "10:00", "11:00", "Entrepreneuriat 1\nMONDO", "Prof. MONDO", "Salle D201");
        createCours(classe, "Jeudi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Jeudi", "13:00", "14:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        createCours(classe, "Jeudi", "14:00", "15:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        createCours(classe, "Jeudi", "15:00", "16:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        createCours(classe, "Jeudi", "16:00", "17:00", "Ethique de l'Ingénieur\nATCHA", "Prof. ATCHA", "Salle A103");
        
        // Vendredi
        createCours(classe, "Vendredi", "08:00", "09:00", "Initiation à la Recherche\nTOUOYEM", "Prof. TOUOYEM", "Salle A101");
        createCours(classe, "Vendredi", "09:00", "10:00", "Initiation à la Recherche\nTOUOYEM", "Prof. TOUOYEM", "Salle A101");
        createCours(classe, "Vendredi", "10:00", "11:00", "Initiation à la Recherche\nTOUOYEM", "Prof. TOUOYEM", "Salle A101");
        createCours(classe, "Vendredi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Vendredi", "13:00", "14:00", "PAUSE", "", "");
        createCours(classe, "Vendredi", "14:00", "15:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        createCours(classe, "Vendredi", "15:00", "16:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        createCours(classe, "Vendredi", "16:00", "17:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        
        // Samedi
        createCours(classe, "Samedi", "08:00", "09:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        createCours(classe, "Samedi", "09:00", "10:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        createCours(classe, "Samedi", "10:00", "11:00", "Concepts de bases de la sécurité informatique\nMOYOU", "Prof. MOYOU", "Lab Info 3");
        createCours(classe, "Samedi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "13:00", "14:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "14:00", "15:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "15:00", "16:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "16:00", "17:00", "PAUSE", "", "");
    }
    
    // MANAGEMENT 1ÈRE ANNÉE
    private void createCoursManagement1(Classe classe) {
        // Lundi
        createCours(classe, "Lundi", "08:00", "09:00", "Comptabilité Générale\nNKOLO", "Prof. NKOLO", "Salle D201");
        createCours(classe, "Lundi", "09:00", "10:00", "Comptabilité Générale\nNKOLO", "Prof. NKOLO", "Salle D201");
        createCours(classe, "Lundi", "10:00", "11:00", "Marketing Stratégique\nMANGA", "Prof. MANGA", "Salle D202");
        createCours(classe, "Lundi", "11:00", "12:00", "Marketing Stratégique\nMANGA", "Prof. MANGA", "Salle D202");
        createCours(classe, "Lundi", "13:00", "14:00", "Économie d'Entreprise\nMVONDO", "Prof. MVONDO", "Salle D203");
        createCours(classe, "Lundi", "14:00", "15:00", "Économie d'Entreprise\nMVONDO", "Prof. MVONDO", "Salle D203");
        createCours(classe, "Lundi", "15:00", "16:00", "Économie d'Entreprise\nMVONDO", "Prof. MVONDO", "Salle D203");
        createCours(classe, "Lundi", "16:00", "17:00", "PAUSE", "", "");
        
        // Mardi
        createCours(classe, "Mardi", "08:00", "09:00", "Gestion des Ressources Humaines\nFOUDA", "Prof. FOUDA", "Salle D201");
        createCours(classe, "Mardi", "09:00", "10:00", "Gestion des Ressources Humaines\nFOUDA", "Prof. FOUDA", "Salle D201");
        createCours(classe, "Mardi", "10:00", "11:00", "Droit des Affaires\nNANA", "Prof. NANA", "Salle D204");
        createCours(classe, "Mardi", "11:00", "12:00", "Droit des Affaires\nNANA", "Prof. NANA", "Salle D204");
        createCours(classe, "Mardi", "13:00", "14:00", "Statistiques Appliquées\nEBODE", "Prof. EBODE", "Salle D205");
        createCours(classe, "Mardi", "14:00", "15:00", "Statistiques Appliquées\nEBODE", "Prof. EBODE", "Salle D205");
        createCours(classe, "Mardi", "15:00", "16:00", "Finance d'Entreprise\nTCHUENTE", "Prof. TCHUENTE", "Salle D206");
        createCours(classe, "Mardi", "16:00", "17:00", "Finance d'Entreprise\nTCHUENTE", "Prof. TCHUENTE", "Salle D206");
        
        // Mercredi
        createCours(classe, "Mercredi", "08:00", "09:00", "Comptabilité Analytique\nNKOLO", "Prof. NKOLO", "Salle D201");
        createCours(classe, "Mercredi", "09:00", "10:00", "Comptabilité Analytique\nNKOLO", "Prof. NKOLO", "Salle D201");
        createCours(classe, "Mercredi", "10:00", "11:00", "Marketing Digital\nMANGA", "Prof. MANGA", "Salle D202");
        createCours(classe, "Mercredi", "11:00", "12:00", "Marketing Digital\nMANGA", "Prof. MANGA", "Salle D202");
        createCours(classe, "Mercredi", "13:00", "14:00", "Projet d'Entreprise\nNGONO", "Prof. NGONO", "Salle D206");
        createCours(classe, "Mercredi", "14:00", "15:00", "Projet d'Entreprise\nNGONO", "Prof. NGONO", "Salle D206");
        createCours(classe, "Mercredi", "15:00", "16:00", "Projet d'Entreprise\nNGONO", "Prof. NGONO", "Salle D206");
        createCours(classe, "Mercredi", "16:00", "17:00", "PAUSE", "", "");
        
        // Jeudi
        createCours(classe, "Jeudi", "08:00", "09:00", "Économie Internationale\nMVONDO", "Prof. MVONDO", "Salle D203");
        createCours(classe, "Jeudi", "09:00", "10:00", "Économie Internationale\nMVONDO", "Prof. MVONDO", "Salle D203");
        createCours(classe, "Jeudi", "10:00", "11:00", "Management Stratégique\nFOUDA", "Prof. FOUDA", "Salle D201");
        createCours(classe, "Jeudi", "11:00", "12:00", "Management Stratégique\nFOUDA", "Prof. FOUDA", "Salle D201");
        createCours(classe, "Jeudi", "13:00", "14:00", "Droit Commercial\nNANA", "Prof. NANA", "Salle D204");
        createCours(classe, "Jeudi", "14:00", "15:00", "Droit Commercial\nNANA", "Prof. NANA", "Salle D204");
        createCours(classe, "Jeudi", "15:00", "16:00", "Communication d'Entreprise\nABENA", "Prof. ABENA", "Salle D207");
        createCours(classe, "Jeudi", "16:00", "17:00", "Communication d'Entreprise\nABENA", "Prof. ABENA", "Salle D207");
        
        // Vendredi
        createCours(classe, "Vendredi", "08:00", "09:00", "Contrôle de Gestion\nEBODE", "Prof. EBODE", "Salle D205");
        createCours(classe, "Vendredi", "09:00", "10:00", "Contrôle de Gestion\nEBODE", "Prof. EBODE", "Salle D205");
        createCours(classe, "Vendredi", "10:00", "11:00", "Anglais des Affaires\nKAMGA", "Prof. KAMGA", "Salle D208");
        createCours(classe, "Vendredi", "11:00", "12:00", "Anglais des Affaires\nKAMGA", "Prof. KAMGA", "Salle D208");
        createCours(classe, "Vendredi", "13:00", "14:00", "Atelier Comptabilité\nNKOLO", "Prof. NKOLO", "Lab Compta");
        createCours(classe, "Vendredi", "14:00", "15:00", "Atelier Comptabilité\nNKOLO", "Prof. NKOLO", "Lab Compta");
        createCours(classe, "Vendredi", "15:00", "16:00", "Atelier Comptabilité\nNKOLO", "Prof. NKOLO", "Lab Compta");
        createCours(classe, "Vendredi", "16:00", "17:00", "PAUSE", "", "");
        
        // Samedi
        createCours(classe, "Samedi", "08:00", "09:00", "Système d'Information\nBIYA", "Prof. BIYA", "Lab Info 1");
        createCours(classe, "Samedi", "09:00", "10:00", "Système d'Information\nBIYA", "Prof. BIYA", "Lab Info 1");
        createCours(classe, "Samedi", "10:00", "11:00", "Système d'Information\nBIYA", "Prof. BIYA", "Lab Info 1");
        createCours(classe, "Samedi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "13:00", "14:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "14:00", "15:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "15:00", "16:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "16:00", "17:00", "PAUSE", "", "");
    }
    
    // INGÉNIEUR 2ÈME ANNÉE
    private void createCoursIngenieur2(Classe classe) {
        // Lundi
        createCours(classe, "Lundi", "08:00", "09:00", "Réseaux Avancés\nFOTSO", "Prof. FOTSO", "Lab Réseau");
        createCours(classe, "Lundi", "09:00", "10:00", "Réseaux Avancés\nFOTSO", "Prof. FOTSO", "Lab Réseau");
        createCours(classe, "Lundi", "10:00", "11:00", "Base de Données\nTCHUENTE", "Prof. TCHUENTE", "Lab Info 2");
        createCours(classe, "Lundi", "11:00", "12:00", "Base de Données\nTCHUENTE", "Prof. TCHUENTE", "Lab Info 2");
        createCours(classe, "Lundi", "13:00", "14:00", "Génie Logiciel\nESSOMBA", "Prof. ESSOMBA", "Salle B201");
        createCours(classe, "Lundi", "14:00", "15:00", "Génie Logiciel\nESSOMBA", "Prof. ESSOMBA", "Salle B201");
        createCours(classe, "Lundi", "15:00", "16:00", "Génie Logiciel\nESSOMBA", "Prof. ESSOMBA", "Salle B201");
        createCours(classe, "Lundi", "16:00", "17:00", "PAUSE", "", "");
        
        // Mardi
        createCours(classe, "Mardi", "08:00", "09:00", "Intelligence Artificielle\nABDOURAMAN", "Prof. ABDOURAMAN", "Lab IA");
        createCours(classe, "Mardi", "09:00", "10:00", "Intelligence Artificielle\nABDOURAMAN", "Prof. ABDOURAMAN", "Lab IA");
        createCours(classe, "Mardi", "10:00", "11:00", "Intelligence Artificielle\nABDOURAMAN", "Prof. ABDOURAMAN", "Lab IA");
        createCours(classe, "Mardi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Mardi", "13:00", "14:00", "Systèmes Embarqués\nONDOA", "Prof. ONDOA", "Lab Elec");
        createCours(classe, "Mardi", "14:00", "15:00", "Systèmes Embarqués\nONDOA", "Prof. ONDOA", "Lab Elec");
        createCours(classe, "Mardi", "15:00", "16:00", "Architecture Logicielle\nKAMGA", "Prof. KAMGA", "Salle B202");
        createCours(classe, "Mardi", "16:00", "17:00", "Architecture Logicielle\nKAMGA", "Prof. KAMGA", "Salle B202");
        
        // Mercredi
        createCours(classe, "Mercredi", "08:00", "09:00", "Développement Web\nTCHUENTE", "Prof. TCHUENTE", "Lab Info 3");
        createCours(classe, "Mercredi", "09:00", "10:00", "Développement Web\nTCHUENTE", "Prof. TCHUENTE", "Lab Info 3");
        createCours(classe, "Mercredi", "10:00", "11:00", "Développement Web\nTCHUENTE", "Prof. TCHUENTE", "Lab Info 3");
        createCours(classe, "Mercredi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Mercredi", "13:00", "14:00", "Sécurité Informatique\nMOYOU", "Prof. MOYOU", "Salle B203");
        createCours(classe, "Mercredi", "14:00", "15:00", "Sécurité Informatique\nMOYOU", "Prof. MOYOU", "Salle B203");
        createCours(classe, "Mercredi", "15:00", "16:00", "Sécurité Informatique\nMOYOU", "Prof. MOYOU", "Salle B203");
        createCours(classe, "Mercredi", "16:00", "17:00", "PAUSE", "", "");
        
        // Jeudi
        createCours(classe, "Jeudi", "08:00", "09:00", "Traitement du Signal\nONDOA", "Prof. ONDOA", "Salle B204");
        createCours(classe, "Jeudi", "09:00", "10:00", "Traitement du Signal\nONDOA", "Prof. ONDOA", "Salle B204");
        createCours(classe, "Jeudi", "10:00", "11:00", "Gestion de Projet\nMONDO", "Prof. MONDO", "Salle B205");
        createCours(classe, "Jeudi", "11:00", "12:00", "Gestion de Projet\nMONDO", "Prof. MONDO", "Salle B205");
        createCours(classe, "Jeudi", "13:00", "14:00", "Anglais Technique 2\nABENA", "Prof. ABENA", "Salle B206");
        createCours(classe, "Jeudi", "14:00", "15:00", "Anglais Technique 2\nABENA", "Prof. ABENA", "Salle B206");
        createCours(classe, "Jeudi", "15:00", "16:00", "Projet Intégré\nBIYA", "Prof. BIYA", "Lab Projet");
        createCours(classe, "Jeudi", "16:00", "17:00", "Projet Intégré\nBIYA", "Prof. BIYA", "Lab Projet");
        
        // Vendredi
        createCours(classe, "Vendredi", "08:00", "09:00", "Cloud Computing\nTAMKO", "Prof. TAMKO", "Lab Cloud");
        createCours(classe, "Vendredi", "09:00", "10:00", "Cloud Computing\nTAMKO", "Prof. TAMKO", "Lab Cloud");
        createCours(classe, "Vendredi", "10:00", "11:00", "Cloud Computing\nTAMKO", "Prof. TAMKO", "Lab Cloud");
        createCours(classe, "Vendredi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Vendredi", "13:00", "14:00", "TP Développement\nESSOMBA", "Prof. ESSOMBA", "Lab Info 4");
        createCours(classe, "Vendredi", "14:00", "15:00", "TP Développement\nESSOMBA", "Prof. ESSOMBA", "Lab Info 4");
        createCours(classe, "Vendredi", "15:00", "16:00", "TP Développement\nESSOMBA", "Prof. ESSOMBA", "Lab Info 4");
        createCours(classe, "Vendredi", "16:00", "17:00", "PAUSE", "", "");
        
        // Samedi
        createCours(classe, "Samedi", "08:00", "09:00", "Recherche Opérationnelle\nFOTSO", "Prof. FOTSO", "Salle B207");
        createCours(classe, "Samedi", "09:00", "10:00", "Recherche Opérationnelle\nFOTSO", "Prof. FOTSO", "Salle B207");
        createCours(classe, "Samedi", "10:00", "11:00", "Recherche Opérationnelle\nFOTSO", "Prof. FOTSO", "Salle B207");
        createCours(classe, "Samedi", "11:00", "12:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "13:00", "14:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "14:00", "15:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "15:00", "16:00", "PAUSE", "", "");
        createCours(classe, "Samedi", "16:00", "17:00", "PAUSE", "", "");
    }
    
    private void createCours(Classe classe, String jour, String heureDebut, String heureFin, 
                            String matiere, String professeur, String salle) {
        Cours cours = new Cours();
        cours.setClasse(classe);
        cours.setJourSemaine(jour);
        cours.setHeureDebut(java.time.LocalTime.parse(heureDebut));
        cours.setHeureFin(java.time.LocalTime.parse(heureFin));
        cours.setMatiere(matiere);
        cours.setProfesseur(professeur);
        cours.setSalle(salle);
        coursRepository.save(cours);
    }
}
