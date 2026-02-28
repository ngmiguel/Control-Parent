import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentListItem } from '../../core/models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly API_URL = 'http://localhost:8080/api/students';

  constructor(private http: HttpClient) {}

  // Récupérer les enfants rattachés au parent
  getEnfantsParParent(phone: string): Observable<StudentListItem[]> {
    return this.http.get<StudentListItem[]>(`${this.API_URL}/parent/${phone}`);
  }

  // Télécharger les notes (PDF JasperReports)
  downloadNotes(matricule: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/${matricule}/notes/pdf`, {
      responseType: 'blob' // Important pour les fichiers PDF
    });
  }

  // Télécharger l'emploi du temps (PDF)
  downloadEmploiDuTemps(classe: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/classe/${classe}/emploi-du-temps/pdf`, {
      responseType: 'blob'
    });
  }
}