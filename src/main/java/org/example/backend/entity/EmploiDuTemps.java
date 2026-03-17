package org.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "emplois_du_temps")
public class EmploiDuTemps {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String libelle;
    
    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;
    
    @Column(nullable = false, length = 1000)
    private String url;

    public EmploiDuTemps() {
    }

    public EmploiDuTemps(Long id, String libelle, Classe classe, String url) {
        this.id = id;
        this.libelle = libelle;
        this.classe = classe;
        this.url = url;
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

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
