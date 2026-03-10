package org.example.backend.dto;

public class EmploiDuTempsDTO {
    
    private Long id;
    private String libelle;
    private String url;
    private String classeLibelle;
    private Long classeId;

    public EmploiDuTempsDTO() {
    }

    public EmploiDuTempsDTO(Long id, String libelle, String url, String classeLibelle, Long classeId) {
        this.id = id;
        this.libelle = libelle;
        this.url = url;
        this.classeLibelle = classeLibelle;
        this.classeId = classeId;
    }

    // Getters et Setters
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClasseLibelle() {
        return classeLibelle;
    }

    public void setClasseLibelle(String classeLibelle) {
        this.classeLibelle = classeLibelle;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }
}
