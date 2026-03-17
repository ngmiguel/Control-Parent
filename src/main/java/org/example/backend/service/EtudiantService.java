package org.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.backend.dto.EtudiantDTO;
import org.example.backend.dto.NoteDTO;
import org.example.backend.entity.Etudiant;
import org.example.backend.entity.Inscription;
import org.example.backend.entity.Note;
import org.example.backend.repository.EtudiantRepository;
import org.springframework.stereotype.Service;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final SmsService smsService;

    public EtudiantService(EtudiantRepository etudiantRepository, SmsService smsService) {
        this.etudiantRepository = etudiantRepository;
        this.smsService = smsService;
    }

    /**
     * Récupère la liste des enfants d'un parent par son numéro de téléphone
     */
    public List<EtudiantDTO> getEnfantsParTelephone(String telephone) {
        // Normaliser le téléphone
        String telNormalise = smsService.normaliserTelephone(telephone);

        // Rechercher les étudiants
        List<Etudiant> etudiants = etudiantRepository
                .findByTelephonePereContainingOrTelephoneMereContaining(telNormalise, telNormalise);

        // Si aucun résultat avec normalisation, essayer sans
        if (etudiants.isEmpty()) {
            etudiants = etudiantRepository
                    .findByTelephonePereContainingOrTelephoneMereContaining(telephone, telephone);
        }

        // Convertir en DTO
        return etudiants.stream()
                .map(e -> convertToDTOWithParent(e, telNormalise.isEmpty() ? telephone : telNormalise))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un étudiant par son matricule
     */
    public Etudiant getEtudiantByMatricule(String matricule) {
        return etudiantRepository.findAll().stream()
                .filter(e -> e.getMatricule().equals(matricule))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));
    }

    /**
     * Récupère les notes d'un étudiant
     */
    public List<NoteDTO> getNotesEtudiant(String matricule) {
        Etudiant etudiant = getEtudiantByMatricule(matricule);

        List<NoteDTO> notes = new ArrayList<>();

        if (etudiant.getInscriptions() != null) {
            for (Inscription inscription : etudiant.getInscriptions()) {
                if (inscription.getNotes() != null) {
                    for (Note note : inscription.getNotes()) {
                        NoteDTO noteDTO = new NoteDTO(
                                note.getLibelleMatiere(),
                                inscription.getCodeUE(),
                                note.getTypeEvaluation().name(),
                                note.getValeur(),
                                inscription.getAnneeAcademique(),
                                inscription.getSemestre()
                        );
                        // Ajouter les pourcentages depuis l'inscription
                        noteDTO.setPourcentageCC(inscription.getPourcentageCC());
                        noteDTO.setPourcentageSN(inscription.getPourcentageSN());
                        noteDTO.setPourcentageTP(inscription.getPourcentageTP());
                        notes.add(noteDTO);
                    }
                }
            }
        }

        return notes;
    }

    /**
     * Récupère les notes d'un étudiant pour une période spécifique
     */
    public List<NoteDTO> getNotesEtudiantParPeriode(String matricule, String anneeAcademique, String semestre) {
        return getNotesEtudiant(matricule).stream()
                .filter(note -> note.getAnneeAcademique().equals(anneeAcademique)
                && note.getSemestre().equals(semestre))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les périodes disponibles pour un étudiant
     */
    public List<org.example.backend.dto.PeriodeDTO> getPeriodesDisponibles(String matricule) {
        Etudiant etudiant = getEtudiantByMatricule(matricule);

        return etudiant.getInscriptions().stream()
                .map(inscription -> new org.example.backend.dto.PeriodeDTO(
                inscription.getAnneeAcademique(),
                inscription.getSemestre()
        ))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Etudiant en DTO avec le nom du parent connecté
     */
    private EtudiantDTO convertToDTOWithParent(Etudiant etudiant, String telephone) {
        EtudiantDTO dto = new EtudiantDTO(
                etudiant.getId(),
                etudiant.getMatricule(),
                etudiant.getNom(),
                etudiant.getPrenom(),
                etudiant.getDateNaissance(),
                etudiant.getClasse().getLibelle(),
                etudiant.getClasse().getFiliere(),
                etudiant.getClasse().getNiveau(),
                etudiant.getClasse().getId()
        );
        // Déterminer si le numéro connecté est celui du père ou de la mère
        String telPere = etudiant.getTelephonePere() != null ? etudiant.getTelephonePere() : "";
        String telMere = etudiant.getTelephoneMere() != null ? etudiant.getTelephoneMere() : "";
        String telBrut = telephone.replaceAll("[\\s()\\-\\+]", "");

        if (telPere.contains(telBrut) || telPere.contains(telephone)) {
            dto.setNomParent(etudiant.getNomPere());
        } else if (telMere.contains(telBrut) || telMere.contains(telephone)) {
            dto.setNomParent(etudiant.getNomMere());
        } else {
            dto.setNomParent(etudiant.getNomPere());
        }
        return dto;
    }

}
