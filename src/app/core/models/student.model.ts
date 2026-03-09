export interface Student {
  matricule: string;
  nom: string;
  prenom: string;
  dateNaissance: Date;
  nomPere: string;
  nomMere: string;
  classe: string;
  photo?: string;
}

export interface StudentListItem {
  matricule: string;
  nom: string;
  prenom: string;
  classe: string;
  photo?: string;
}