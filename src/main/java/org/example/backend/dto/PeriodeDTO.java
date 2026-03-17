package org.example.backend.dto;

public class PeriodeDTO {
    
    private String anneeAcademique;
    private String semestre;

    public PeriodeDTO() {
    }

    public PeriodeDTO(String anneeAcademique, String semestre) {
        this.anneeAcademique = anneeAcademique;
        this.semestre = semestre;
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
}
