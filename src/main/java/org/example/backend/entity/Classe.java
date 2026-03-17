package org.example.backend.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "classes")
public class Classe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String libelle;
    
    @Column(nullable = false)
    private String filiere;
    
    @Column(nullable = false)
    private String niveau;
    
    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL)
    private List<Etudiant> etudiants;
    
    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL)
    private List<EmploiDuTemps> emploisDuTemps;

    public Classe() {
    }

    public Classe(Long id, String libelle, String filiere, String niveau, List<Etudiant> etudiants, List<EmploiDuTemps> emploisDuTemps) {
        this.id = id;
        this.libelle = libelle;
        this.filiere = filiere;
        this.niveau = niveau;
        this.etudiants = etudiants;
        this.emploisDuTemps = emploisDuTemps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    public List<EmploiDuTemps> getEmploisDuTemps() {
        return emploisDuTemps;
    }

    public void setEmploisDuTemps(List<EmploiDuTemps> emploisDuTemps) {
        this.emploisDuTemps = emploisDuTemps;
    }
}
