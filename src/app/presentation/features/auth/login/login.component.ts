import { Component, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthService } from '../../../../data/api/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements AfterViewInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      phone: ['', [Validators.required, this.cameroonPhoneValidator]]
    });
  }

  ngAfterViewInit() {
    // Initialiser les icônes Lucide
    setTimeout(() => {
      if (typeof (window as any).lucide !== 'undefined') {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }

  // Validateur personnalisé pour les numéros camerounais
  cameroonPhoneValidator(control: any) {
    if (!control.value) {
      return null;
    }

    // Nettoyer le numéro (enlever espaces, parenthèses, tirets, +)
    const cleaned = control.value.replace(/[\s\(\)\-\+]/g, '');
    
    // Vérifier les formats camerounais:
    // - 237XXXXXXXXX (avec indicatif)
    // - 6XXXXXXXX (sans indicatif, commence par 6)
    const cameroonPattern = /^(237)?6[0-9]{8}$/;
    
    if (!cameroonPattern.test(cleaned)) {
      return { invalidCameroonPhone: true };
    }
    
    return null;
  }

  onRequestOtp() {
    if (this.loginForm.get('phone')?.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.cdr.detectChanges();
      
      // Nettoyer le numéro avant de l'envoyer
      const cleanedPhone = this.loginForm.value.phone.replace(/[\s\(\)\-\+]/g, '');
      // Enlever le préfixe 237 si présent
      const telephone = cleanedPhone.startsWith('237') ? cleanedPhone.substring(3) : cleanedPhone;
      
      // Appel API réel
      this.authService.demanderCode(telephone).subscribe({
        next: (response) => {
          // Sauvegarder temporairement le téléphone pour la page de vérification
          localStorage.setItem('temp_phone', telephone);
          this.router.navigate(['/verify-otp']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Erreur lors de l\'envoi du code. Vérifiez votre numéro.';
          this.cdr.detectChanges();
        }
      });
    }
  }
}

