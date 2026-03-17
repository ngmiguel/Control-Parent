import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EmploiDuTemps {
    id: number;
    libelle: string;
    url: string;
    classe: {
        id: number;
        libelle: string;
        filiere: string;
        niveau: string;
    };
}

@Injectable({
    providedIn: 'root'
})
export class EmploiDuTempsService {
    private readonly API_URL = 'http://localhost:8080/api/emploi-du-temps';

    constructor(private http: HttpClient) { }

    // Récupérer l'emploi du temps d'une classe (JSON)
    getEmploiDuTemps(classeId: number): Observable<EmploiDuTemps> {
        return this.http.get<EmploiDuTemps>(`${this.API_URL}/classe/${classeId}`);
    }

    // Télécharger l'emploi du temps PDF
    telechargerEmploiDuTempsPDF(classeId: number): Observable<Blob> {
        return this.http.get(`${this.API_URL}/pdf/${classeId}`, {
            responseType: 'blob'
        });
    }
}