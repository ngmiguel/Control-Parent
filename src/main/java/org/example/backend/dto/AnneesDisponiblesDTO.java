package org.example.backend.dto;

import java.util.List;

public class AnneesDisponiblesDTO {
    
    private List<String> annees;
    private List<String> semestres;

    public AnneesDisponiblesDTO() {
    }

    public AnneesDisponiblesDTO(List<String> annees, List<String> semestres) {
        this.annees = annees;
        this.semestres = semestres;
    }

    public List<String> getAnnees() {
        return annees;
    }

    public void setAnnees(List<String> annees) {
        this.annees = annees;
    }

    public List<String> getSemestres() {
        return semestres;
    }

    public void setSemestres(List<String> semestres) {
        this.semestres = semestres;
    }
}
