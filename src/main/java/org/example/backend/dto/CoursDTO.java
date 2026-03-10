package org.example.backend.dto;

public class CoursDTO {
    
    private String horaire;
    private String lundi;
    private String mardi;
    private String mercredi;
    private String jeudi;
    private String vendredi;
    private String samedi;
    private String colorLundi;
    private String colorMardi;
    private String colorMercredi;
    private String colorJeudi;
    private String colorVendredi;
    private String colorSamedi;

    public CoursDTO() {
    }

    public CoursDTO(String horaire, String lundi, String mardi, String mercredi, 
                    String jeudi, String vendredi, String samedi) {
        this.horaire = horaire;
        this.lundi = lundi;
        this.mardi = mardi;
        this.mercredi = mercredi;
        this.jeudi = jeudi;
        this.vendredi = vendredi;
        this.samedi = samedi;
    }
    
    public CoursDTO(String horaire, String lundi, String mardi, String mercredi, 
                    String jeudi, String vendredi, String samedi,
                    String colorLundi, String colorMardi, String colorMercredi,
                    String colorJeudi, String colorVendredi, String colorSamedi) {
        this.horaire = horaire;
        this.lundi = lundi;
        this.mardi = mardi;
        this.mercredi = mercredi;
        this.jeudi = jeudi;
        this.vendredi = vendredi;
        this.samedi = samedi;
        this.colorLundi = colorLundi;
        this.colorMardi = colorMardi;
        this.colorMercredi = colorMercredi;
        this.colorJeudi = colorJeudi;
        this.colorVendredi = colorVendredi;
        this.colorSamedi = colorSamedi;
    }

    public String getHoraire() {
        return horaire;
    }

    public void setHoraire(String horaire) {
        this.horaire = horaire;
    }

    public String getLundi() {
        return lundi;
    }

    public void setLundi(String lundi) {
        this.lundi = lundi;
    }

    public String getMardi() {
        return mardi;
    }

    public void setMardi(String mardi) {
        this.mardi = mardi;
    }

    public String getMercredi() {
        return mercredi;
    }

    public void setMercredi(String mercredi) {
        this.mercredi = mercredi;
    }

    public String getJeudi() {
        return jeudi;
    }

    public void setJeudi(String jeudi) {
        this.jeudi = jeudi;
    }

    public String getVendredi() {
        return vendredi;
    }

    public void setVendredi(String vendredi) {
        this.vendredi = vendredi;
    }

    public String getSamedi() {
        return samedi;
    }

    public void setSamedi(String samedi) {
        this.samedi = samedi;
    }

    public String getColorLundi() {
        return colorLundi;
    }

    public void setColorLundi(String colorLundi) {
        this.colorLundi = colorLundi;
    }

    public String getColorMardi() {
        return colorMardi;
    }

    public void setColorMardi(String colorMardi) {
        this.colorMardi = colorMardi;
    }

    public String getColorMercredi() {
        return colorMercredi;
    }

    public void setColorMercredi(String colorMercredi) {
        this.colorMercredi = colorMercredi;
    }

    public String getColorJeudi() {
        return colorJeudi;
    }

    public void setColorJeudi(String colorJeudi) {
        this.colorJeudi = colorJeudi;
    }

    public String getColorVendredi() {
        return colorVendredi;
    }

    public void setColorVendredi(String colorVendredi) {
        this.colorVendredi = colorVendredi;
    }

    public String getColorSamedi() {
        return colorSamedi;
    }

    public void setColorSamedi(String colorSamedi) {
        this.colorSamedi = colorSamedi;
    }
}
