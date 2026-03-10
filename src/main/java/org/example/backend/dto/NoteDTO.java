package org.example.backend.dto;

public class NoteDTO {
    
    private String libelleMatiere;
    private String codeUE;
    private String typeEvaluation;
    private Double valeur;
    private String anneeAcademique;
    private String semestre;
    private Integer pourcentageCC;
    private Integer pourcentageSN;
    private Integer pourcentageTP;

    public NoteDTO() {
    }

    public NoteDTO(String libelleMatiere, String codeUE, String typeEvaluation, 
                   Double valeur, String anneeAcademique, String semestre) {
        this.libelleMatiere = libelleMatiere;
        this.codeUE = codeUE;
        this.typeEvaluation = typeEvaluation;
        this.valeur = valeur;
        this.anneeAcademique = anneeAcademique;
        this.semestre = semestre;
    }

    // Getters et Setters
    public String getLibelleMatiere() {
        return libelleMatiere;
    }

    public void setLibelleMatiere(String libelleMatiere) {
        this.libelleMatiere = libelleMatiere;
    }

    public String getCodeUE() {
        return codeUE;
    }

    public void setCodeUE(String codeUE) {
        this.codeUE = codeUE;
    }

    public String getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(String typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
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

    public Integer getPourcentageCC() {
        return pourcentageCC;
    }

    public void setPourcentageCC(Integer pourcentageCC) {
        this.pourcentageCC = pourcentageCC;
    }

    public Integer getPourcentageSN() {
        return pourcentageSN;
    }

    public void setPourcentageSN(Integer pourcentageSN) {
        this.pourcentageSN = pourcentageSN;
    }

    public Integer getPourcentageTP() {
        return pourcentageTP;
    }

    public void setPourcentageTP(Integer pourcentageTP) {
        this.pourcentageTP = pourcentageTP;
    }
}
