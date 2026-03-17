import { Component, ChangeDetectorRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../data/api/auth.service';
import { Subject, interval, takeUntil } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements AfterViewInit, OnDestroy {
  loginForm: FormGroup;
  step: 'PHONE' | 'OTP' = 'PHONE';
  isLoading = false;
  errorMessage = '';
  parentName: string = '';

  // Timer pour l'expiration du code (15 minutes)
  codeExpirationTime = 0;
  codeExpirationTimer = '';
  private destroy$ = new Subject<void>();
  private timerDestroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.timerDestroy$.next();
    this.timerDestroy$.complete();
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

      // Appel API réel
      this.authService.demanderCode(cleanedPhone).subscribe({
        next: (response) => {
          console.log('Code OTP demandé avec succès:', response);
          this.step = 'OTP';
          this.isLoading = false;
          this.errorMessage = '';

          // Démarrer le timer d'expiration (15 minutes = 900 secondes)
          this.startExpirationTimer(900);

          this.cdr.detectChanges();

          // Réinitialiser les icônes après changement de step
          setTimeout(() => {
            if (typeof (window as any).lucide !== 'undefined') {
              (window as any).lucide.createIcons();
            }
          }, 100);
        },
        error: (error) => {
          console.error('Erreur lors de la demande de code:', error);
          this.isLoading = false;
          this.errorMessage = 'Erreur lors de l\'envoi du code. Vérifiez votre numéro.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  onVerifyOtp() {
    const { phone, code } = this.loginForm.value;
    console.log('Tentative de vérification OTP:', { phone, code });

    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    // Nettoyer le numéro avant de l'envoyer
    const cleanedPhone = phone.replace(/[\s\(\)\-\+]/g, '');
    console.log('Numéro nettoyé:', cleanedPhone);

    // Appel API réel
    this.authService.verifierCode(cleanedPhone, code).subscribe({
      next: (response) => {
        console.log('Authentification réussie:', response);
        // Les informations sont déjà stockées par le service
        console.log('Token stocké:', response.token.substring(0, 20) + '...');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Erreur lors de la vérification du code:', error);
        this.isLoading = false;
        this.errorMessage = 'Code invalide ou expiré. Veuillez réessayer.';
        this.cdr.detectChanges();
      }
    });
  }

  // Démarrer le timer d'expiration du code
  private startExpirationTimer(seconds: number) {
    // Annuler le timer précédent si existant
    this.timerDestroy$.next();

    this.codeExpirationTime = seconds;
    this.updateTimerDisplay();
    this.cdr.detectChanges();

    interval(1000)
      .pipe(takeUntil(this.timerDestroy$))
      .subscribe(() => {
        this.codeExpirationTime--;
        this.updateTimerDisplay();
        this.cdr.detectChanges();

        if (this.codeExpirationTime <= 0) {
          this.timerDestroy$.next();
          this.errorMessage = 'Le code a expiré. Veuillez demander un nouveau code.';
          this.step = 'PHONE';
          this.cdr.detectChanges();
        }
      });
  }

  // Mettre à jour l'affichage du timer
  private updateTimerDisplay() {
    const minutes = Math.floor(this.codeExpirationTime / 60);
    const seconds = this.codeExpirationTime % 60;
    this.codeExpirationTimer = `${minutes}:${seconds.toString().padStart(2, '0')}`;
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
