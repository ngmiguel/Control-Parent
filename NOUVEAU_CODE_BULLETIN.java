// Code à remplacer dans genererBulletinSemestre - Section calcul des notes

for (Map.Entry<String, List<NoteDTO>> entry : notesParMatiere.entrySet()) {
    String matiere = entry.getKey();
    List<NoteDTO> notesMatiere = entry.getValue();
    
    Map<String, Object> ligne = new HashMap<>();
    ligne.put("matiere", matiere);
    ligne.put("codeUE", notesMatiere.get(0).getCodeUE());
    
    // Récupérer les pourcentages depuis la première note
    Integer pourcentageCC = notesMatiere.get(0).getPourcentageCC();
    Integer pourcentageSN = notesMatiere.get(0).getPourcentageSN();
    Integer pourcentageTP = notesMatiere.get(0).getPourcentageTP();
    
    // Récupérer les notes par type
    Double noteCC = notesMatiere.stream()
            .filter(n -> "CC".equals(n.getTypeEvaluation()))
            .map(NoteDTO::getValeur)
            .findFirst().orElse(null);
    
    Double noteSN = notesMatiere.stream()
            .filter(n -> "SN".equals(n.getTypeEvaluation()))
            .map(NoteDTO::getValeur)
            .findFirst().orElse(null);
    
    Double noteTP = notesMatiere.stream()
            .filter(n -> "TP".equals(n.getTypeEvaluation()))
            .map(NoteDTO::getValeur)
            .findFirst().orElse(null);
    
    // Afficher les pourcentages et notes
    ligne.put("pourcentageCC", pourcentageCC + "%");
    ligne.put("noteCC", noteCC != null ? String.format("%.2f", noteCC) : "-");
    ligne.put("pourcentageSN", pourcentageSN + "%");
    ligne.put("noteSN", noteSN != null ? String.format("%.2f", noteSN) : "-");
    ligne.put("pourcentageTP", pourcentageTP + "%");
    ligne.put("noteTP", noteTP != null ? String.format("%.2f", noteTP) : "-");
    
    // Calculer la moyenne PONDÉRÉE de la matière
    double moyennePonderee = 0;
    if (noteCC != null && noteSN != null && noteTP != null) {
        moyennePonderee = (noteCC * pourcentageCC / 100.0) + 
                         (noteSN * pourcentageSN / 100.0) + 
                         (noteTP * pourcentageTP / 100.0);
    } else if (noteCC != null && noteSN != null) {
        // Si pas de TP, recalculer les pourcentages
        double totalPourcentage = pourcentageCC + pourcentageSN;
        moyennePonderee = (noteCC * pourcentageCC / totalPourcentage) + 
                         (noteSN * pourcentageSN / totalPourcentage);
    } else {
        // Fallback: moyenne simple
        double somme = 0;
        int count = 0;
        if (noteCC != null) { somme += noteCC; count++; }
        if (noteSN != null) { somme += noteSN; count++; }
        if (noteTP != null) { somme += noteTP; count++; }
        moyennePonderee = count > 0 ? somme / count : 0;
    }
    
    ligne.put("moyenne", String.format("%.2f", moyennePonderee));
    
    bulletinData.add(ligne);
    sommeGenerale += moyennePonderee;
    nombreMatieres++;
}
