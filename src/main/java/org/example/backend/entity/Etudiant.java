package org.example.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "etudiants")
public class Etudiant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String matricule;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(nullable = false)
    private LocalDate dateNaissance;
    
    @Column(nullable = false)
    private String nomPere;
    
    @Column(nullable = false)
    private String nomMere;
    
    @Column(nullable = false, length = 500)
    private String telephonePere;
    
    @Column(nullable = false, length = 500)
    private String telephoneMere;
    
    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;
    
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions;

    public Etudiant() {
    }

    public Etudiant(Long id, String matricule, String nom, String prenom, LocalDate dateNaissance, String nomPere, String nomMere, String telephonePere, String telephoneMere, Classe classe, List<Inscription> inscriptions) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nomPere = nomPere;
        this.nomMere = nomMere;
        this.telephonePere = telephonePere;
        this.telephoneMere = telephoneMere;
        this.classe = classe;
        this.inscriptions = inscriptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNomPere() {
        return nomPere;
    }

    public void setNomPere(String nomPere) {
        this.nomPere = nomPere;
    }

    public String getNomMere() {
        return nomMere;
    }

    public void setNomMere(String nomMere) {
        this.nomMere = nomMere;
    }

    public String getTelephonePere() {
        return telephonePere;
    }

    public void setTelephonePere(String telephonePere) {
        this.telephonePere = telephonePere;
    }

    public String getTelephoneMere() {
        return telephoneMere;
    }

    public void setTelephoneMere(String telephoneMere) {
        this.telephoneMere = telephoneMere;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }
}
