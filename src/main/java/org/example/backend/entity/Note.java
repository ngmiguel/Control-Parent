package org.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvaluation typeEvaluation;
    
    @Column(nullable = false)
    private Double valeur;
    
    @Column(nullable = false)
    private String libelleMatiere;
    
    @ManyToOne
    @JoinColumn(name = "inscription_id", nullable = false)
    private Inscription inscription;
    
    public enum TypeEvaluation {
        CC, SN, TP, RAT
    }

    public Note() {
    }

    public Note(Long id, TypeEvaluation typeEvaluation, Double valeur, String libelleMatiere, Inscription inscription) {
        this.id = id;
        this.typeEvaluation = typeEvaluation;
        this.valeur = valeur;
        this.libelleMatiere = libelleMatiere;
        this.inscription = inscription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeEvaluation getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(TypeEvaluation typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public String getLibelleMatiere() {
        return libelleMatiere;
    }

    public void setLibelleMatiere(String libelleMatiere) {
        this.libelleMatiere = libelleMatiere;
    }

    public Inscription getInscription() {
        return inscription;
    }

    public void setInscription(Inscription inscription) {
        this.inscription = inscription;
    }
}
