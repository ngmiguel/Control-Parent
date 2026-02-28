import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  step: 'PHONE' | 'OTP' = 'PHONE';
  isLoading = false;
  errorMessage = '';
  parentName: string = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{9,}$')]],
      code: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  onRequestOtp() {
    console.log('onRequestOtp appelé');
    
    if (this.loginForm.get('phone')?.valid) {
      this.isLoading = true;
      this.cdr.detectChanges();
      
      // MODE DÉVELOPPEMENT: Simulation sans appel API
      setTimeout(() => {
        this.parentName = 'M. KAMGA Pierre';
        this.step = 'OTP';
        this.isLoading = false;
        this.errorMessage = '';
        this.cdr.detectChanges();
      }, 500);
      
      /* MODE PRODUCTION: Décommenter pour utiliser l'API réelle
      this.authService.requestOtp(this.loginForm.value.phone).subscribe({
        next: (response) => {
          if (response.parentName) {
            this.parentName = response.parentName;
          }
          this.step = 'OTP';
          this.isLoading = false;
          this.errorMessage = '';
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = "Numéro non trouvé ou erreur lors de l'envoi du SMS.";
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
      */
    }
  }

  onVerifyOtp() {
    const { phone, code } = this.loginForm.value;
    this.isLoading = true;
    this.cdr.detectChanges();
    
    // MODE DÉVELOPPEMENT: Simulation sans appel API
    setTimeout(() => {
      localStorage.setItem('user_phone', phone);
      localStorage.setItem('parent_name', this.parentName);
      this.router.navigate(['/dashboard']);
    }, 500);
    
    /* MODE PRODUCTION: Décommenter pour utiliser l'API réelle
    this.authService.verifyOtp(phone, code).subscribe({
      next: (response) => {
        localStorage.setItem('user_phone', response.phone);
        localStorage.setItem('parent_name', response.parentName);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = "Code incorrect ou expiré.";
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
    */
  }

  openGmailCompose() {
    const to = 'zacharie.melongo@saintjeaningenieur.org';
    const subject = 'Demande d\'ajout de numéro - Suivi Académique';
    const body = `Bonjour,

Je souhaite ajouter mon numéro de téléphone pour accéder au suivi académique de mes enfants.

Mon numéro : 
Nom complet : 
Nom(s) de(s) enfant(s) : 

Merci.`;

    const gmailUrl = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodeURIComponent(to)}&su=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    window.open(gmailUrl, '_blank');
  }
}
