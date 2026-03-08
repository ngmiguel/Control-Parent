import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthResponse, OtpRequest, OtpVerify } from '../../core/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth'; // Remplace par ton URL

  constructor(private http: HttpClient) {}

  // Demander l'envoi du code OTP
  requestOtp(phone: string): Observable<any> {
    return this.http.post(`${this.API_URL}/request-otp`, { phone });
  }

  // Vérifier le code OTP reçu
  verifyOtp(phone: string, code: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/verify-otp`, { phone, code });
  }
}