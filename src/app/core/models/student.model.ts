export interface Student {
  id: number;
  matricule: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  classeLibelle: string;
  filiere: string;
  niveau: string;
  classeId: number;
  nomPere?: string;
  nomMere?: string;
  telephonePere?: string;
  telephoneMere?: string;
}

export interface StudentListItem {
  id: number;
  matricule: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  classeLibelle: string;
  filiere: string;
  niveau: string;
  classeId: number;
  nomParent?: string;
}

export interface Note {
  libelleMatiere: string;
  codeUE: string;
  typeEvaluation: 'CC' | 'SN' | 'TP' | 'RAT';
  valeur: number;
  anneeAcademique: string;
  semestre: string;
}

export interface Periode {
  anneeAcademique: string;
  semestre: string;
}

export interface EmploiDuTemps {
  id: number;
  libelle: string;
  url: string;
  classeLibelle: string;
  classeId: number;
}