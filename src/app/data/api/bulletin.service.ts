import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Note {
    id: number;
    typeEvaluation: string;
    valeur: number;
    libelleMatiere: string;
}

export interface Periode {
    anneeAcademique: string;
    semestre: string;
}

@Injectable({
    providedIn: 'root'
})
export class BulletinService {
    private readonly API_URL = 'http://localhost:8080/api/bulletin';

    constructor(private http: HttpClient) { }

    // Récupérer les périodes disponibles pour un étudiant
    getPeriodesDisponibles(matricule: string): Observable<Periode[]> {
        return this.http.get<Periode[]>(`${this.API_URL}/periodes/${matricule}`);
    }

    // Récupérer les notes d'un étudiant (JSON)
    getNotes(matricule: string, anneeAcademique?: string, semestre?: string): Observable<Note[]> {
        let params = new HttpParams();
        if (anneeAcademique) {
            params = params.set('anneeAcademique', anneeAcademique);
        }
        if (semestre) {
            params = params.set('semestre', semestre);
        }

        return this.http.get<Note[]>(`${this.API_URL}/notes/${matricule}`, { params });
    }

    // Télécharger le bulletin PDF
    telechargerBulletinPDF(matricule: string, anneeAcademique?: string, semestre?: string): Observable<Blob> {
        let params = new HttpParams();
        if (anneeAcademique) {
            params = params.set('anneeAcademique', anneeAcademique);
        }
        if (semestre) {
            params = params.set('semestre', semestre);
        }

        const url = `${this.API_URL}/pdf/${matricule}`;
        console.log('🌐 URL de téléchargement:', url);
        console.log('📋 Paramètres:', { anneeAcademique, semestre });

        return this.http.get(url, {
            params,
            responseType: 'blob'
        });
    }
}