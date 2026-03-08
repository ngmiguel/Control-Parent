import { Component, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class LoginComponent implements AfterViewInit {
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
      phone: ['', [Validators.required, this.cameroonPhoneValidator]],
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern('^[0-9]{6}$')]]
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
      this.cdr.detectChanges();
      
      // Nettoyer le numéro avant de l'envoyer
      const cleanedPhone = this.loginForm.value.phone.replace(/[\s\(\)\-\+]/g, '');
      
      // MODE DÉVELOPPEMENT: Simulation sans appel API
      setTimeout(() => {
        this.parentName = 'M. KAMGA Pierre';
        this.step = 'OTP';
        this.isLoading = false;
        this.errorMessage = '';
        this.cdr.detectChanges();
        
        // Réinitialiser les icônes après changement de step
        setTimeout(() => {
          if (typeof (window as any).lucide !== 'undefined') {
            (window as any).lucide.createIcons();
          }
        }, 100);
      }, 500);
    }
  }

  onVerifyOtp() {
    const { phone, code } = this.loginForm.value;
    this.isLoading = true;
    this.cdr.detectChanges();
    
    // Nettoyer le numéro avant de le stocker
    const cleanedPhone = phone.replace(/[\s\(\)\-\+]/g, '');
    
    // MODE DÉVELOPPEMENT: Simulation sans appel API
    setTimeout(() => {
      localStorage.setItem('user_phone', cleanedPhone);
      localStorage.setItem('parent_name', this.parentName);
      this.router.navigate(['/dashboard']);
    }, 500);
  }

  // Empêcher la saisie de caractères non numériques
  onlyNumbers(event: KeyboardEvent): boolean {
    const charCode = event.which ? event.which : event.keyCode;
    // Autoriser uniquement les chiffres (0-9)
    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
      return false;
    }
    return true;
  }
}
