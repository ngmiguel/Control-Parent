package org.example.backend.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "inscriptions")
public class Inscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String codeUE;
    
    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    @Column(nullable = false)
    private String anneeAcademique;
    
    @Column(nullable = false)
    private String semestre;
    
    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL)
    private List<Note> notes;

    public Inscription() {
    }

    public Inscription(Long id, String codeUE, Etudiant etudiant, String anneeAcademique, String semestre, List<Note> notes) {
        this.id = id;
        this.codeUE = codeUE;
        this.etudiant = etudiant;
        this.anneeAcademique = anneeAcademique;
        this.semestre = semestre;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeUE() {
        return codeUE;
    }

    public void setCodeUE(String codeUE) {
        this.codeUE = codeUE;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public String getAnneeAcademique() {
        return anneeAcademique;
    }

    public void setAnneeAcademique(String anneeAcademique) {
        this.anneeAcademique = anneeAcademique;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
