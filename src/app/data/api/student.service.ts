import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentListItem } from '../../core/models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly API_URL = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient) { }

  // Récupérer les enfants rattachés au parent connecté
  getMesEnfants(): Observable<StudentListItem[]> {
    return this.http.get<StudentListItem[]>(`${this.API_URL}/mes-enfants`);
  }

  // Récupérer les détails d'un étudiant
  getEtudiant(matricule: string): Observable<StudentListItem> {
    return this.http.get<StudentListItem>(`${this.API_URL}/${matricule}`);
  }
}