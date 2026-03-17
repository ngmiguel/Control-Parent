package org.example.backend.dto;

public class MatiereNotesDTO {
    
    private String matiere;
    private String codeUE;
    private Double noteCC;
    private Double noteSN;
    private Double noteTP;
    private Double noteRAT;
    private Double moyenne;
    private Integer pourcentageCC;
    private Integer pourcentageSN;
    private Integer pourcentageTP;

    public MatiereNotesDTO() {
    }

    public MatiereNotesDTO(String matiere, String codeUE) {
        this.matiere = matiere;
        this.codeUE = codeUE;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public String getCodeUE() {
        return codeUE;
    }

    public void setCodeUE(String codeUE) {
        this.codeUE = codeUE;
    }

    public Double getNoteCC() {
        return noteCC;
    }

    public void setNoteCC(Double noteCC) {
        this.noteCC = noteCC;
    }

    public Double getNoteSN() {
        return noteSN;
    }

    public void setNoteSN(Double noteSN) {
        this.noteSN = noteSN;
    }

    public Double getNoteTP() {
        return noteTP;
    }

    public void setNoteTP(Double noteTP) {
        this.noteTP = noteTP;
    }

    public Double getNoteRAT() {
        return noteRAT;
    }

    public void setNoteRAT(Double noteRAT) {
        this.noteRAT = noteRAT;
    }

    public Double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Double moyenne) {
        this.moyenne = moyenne;
    }
    
    public String getMoyenneFormatted() {
        return moyenne != null ? String.format("%.2f", moyenne) : "-";
    }
    
    public String getNoteCCFormatted() {
        return noteCC != null ? String.format("%.2f", noteCC) : "-";
    }
    
    public String getNoteSNFormatted() {
        return noteSN != null ? String.format("%.2f", noteSN) : "-";
    }
    
    public String getNoteTPFormatted() {
        return noteTP != null ? String.format("%.2f", noteTP) : "-";
    }
    
    public String getNoteRATFormatted() {
        return noteRAT != null ? String.format("%.2f", noteRAT) : "-";
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
    
    public String getPourcentageCCFormatted() {
        return pourcentageCC != null ? pourcentageCC + "%" : "-";
    }
    
    public String getPourcentageSNFormatted() {
        return pourcentageSN != null ? pourcentageSN + "%" : "-";
    }
    
    public String getPourcentageTPFormatted() {
        return pourcentageTP != null ? pourcentageTP + "%" : "-";
    }
}
