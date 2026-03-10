import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../../core/models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly API_URL = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient) {}

  // Récupérer les enfants rattachés au parent connecté
  getMesEnfants(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.API_URL}/mes-enfants`);
  }

  // Récupérer les détails d'un étudiant
  getEtudiant(matricule: string): Observable<Student> {
    return this.http.get<Student>(`${this.API_URL}/${matricule}`);
  }
}