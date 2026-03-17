import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { AuthResponse } from '../../core/models/auth.model';

interface MessageResponse {
  message: string;
  success: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private tokenSubject = new BehaviorSubject<string | null>(this.getToken());
  public token$ = this.tokenSubject.asObservable();

  private phoneSubject = new BehaviorSubject<string | null>(this.getPhoneNumber());
  public phone$ = this.phoneSubject.asObservable();

  constructor(private http: HttpClient) { }

  // Demander l'envoi du code OTP
  demanderCode(telephone: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.API_URL}/demander-code`, { telephone });
  }

  // Vérifier le code OTP reçu
  verifierCode(telephone: string, code: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/verifier-code`, { telephone, code })
      .pipe(
        tap((response: AuthResponse) => {
          if (response.token) {
            this.setToken(response.token);
            this.setPhoneNumber(telephone);
          }
        })
      );
  }

  // Vérifier l'utilisateur connecté
  me(): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`${this.API_URL}/me`);
  }

  // Déconnexion
  logout(): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.API_URL}/logout`, {})
      .pipe(
        tap(() => {
          this.clearToken();
          this.clearPhoneNumber();
        })
      );
  }

  // Gestion du token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
    this.tokenSubject.next(token);
  }

  clearToken(): void {
    localStorage.removeItem('token');
    this.tokenSubject.next(null);
  }

  // Gestion du numéro de téléphone (session)
  getPhoneNumber(): string | null {
    return localStorage.getItem('user_phone');
  }

  setPhoneNumber(phone: string): void {
    localStorage.setItem('user_phone', phone);
    this.phoneSubject.next(phone);
  }

  clearPhoneNumber(): void {
    localStorage.removeItem('user_phone');
    this.phoneSubject.next(null);
  }

  // Vérifier l'authentification
  isAuthenticated(): boolean {
    const token = this.getToken();
    const phone = this.getPhoneNumber();
    return !!(token && phone);
  }

  // Obtenir les informations de session
  getSessionInfo(): { token: string | null; phone: string | null } {
    return {
      token: this.getToken(),
      phone: this.getPhoneNumber()
    };
  }
}