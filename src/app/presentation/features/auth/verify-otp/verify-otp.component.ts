import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../data/api/auth.service';

@Component({
  selector: 'app-verify-otp',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './verify-otp.component.html',
  styleUrl: './verify-otp.component.scss'
})
export class VerifyOtpComponent implements OnInit, OnDestroy {
  verifyForm: FormGroup;
  telephone = '';
  isLoading = false;
  errorMessage = '';
  timeLeft = 15 * 60;
  timerInterval: any;
  isExpired = false;
  canResend = false;
  resendTimeLeft = 90;
  resendInterval: any;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), Validators.pattern(/^[0-9]{6}$/)]]
    });
  }

  ngOnInit(): void {
    console.log('🔄 Initialisation de la page verify-otp');
    
    this.telephone = localStorage.getItem('temp_phone') || '';
    console.log('📞 Téléphone récupéré:', this.telephone);
    
    if (!this.telephone) {
      console.log('❌ Pas de téléphone, redirection vers login');
      this.router.navigate(['/login']);
      return;
    }

    console.log('⏱️ Démarrage des timers');
    this.startTimer();
    this.startResendTimer();
  }

  ngOnDestroy(): void {
    console.log('🛑 Nettoyage des timers');
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
    if (this.resendInterval) {
      clearInterval(this.resendInterval);
    }
  }

  startTimer() {
    console.log('⏱️ Timer principal démarré - 15:00');
    this.timerInterval = setInterval(() => {
      this.timeLeft--;
      
      if (this.timeLeft % 10 === 0) {
        console.log('⏱️ Timer principal:', this.getFormattedTime());
      }
      
      if (this.timeLeft <= 0) {
        console.log('⏱️ Timer principal expiré');
        this.isExpired = true;
        clearInterval(this.timerInterval);
        this.errorMessage = 'Le code a expiré. Veuillez en demander un nouveau.';
      }
      
      this.cdr.detectChanges();
    }, 1000);
  }

  startResendTimer() {
    console.log('⏱️ Timer de renvoi démarré - 1:30');
    this.resendInterval = setInterval(() => {
      this.resendTimeLeft--;
      
      if (this.resendTimeLeft % 10 === 0) {
        console.log('⏱️ Timer de renvoi:', this.getResendTime());
      }
      
      if (this.resendTimeLeft <= 0) {
        console.log('✅ Bouton renvoyer activé');
        this.canResend = true;
        clearInterval(this.resendInterval);
      }
      
      this.cdr.detectChanges();
    }, 1000);
  }

  getFormattedTime(): string {
    const minutes = Math.floor(this.timeLeft / 60);
    const seconds = this.timeLeft % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  getResendTime(): string {
    const minutes = Math.floor(this.resendTimeLeft / 60);
    const seconds = this.resendTimeLeft % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  isWarning(): boolean {
    return this.timeLeft <= 60;
  }

  onVerifyCode() {
    if (this.verifyForm.valid && !this.isExpired) {
      this.isLoading = true;
      this.errorMessage = '';
      
      const code = this.verifyForm.value.code;
      console.log('🔐 Vérification du code:', code);
      
      this.authService.verifierCode(this.telephone, code).subscribe({
        next: () => {
          console.log('✅ Code vérifié avec succès');
          localStorage.setItem('user_phone', this.telephone);
          localStorage.removeItem('temp_phone');
          clearInterval(this.timerInterval);
          clearInterval(this.resendInterval);
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error('❌ Erreur de vérification:', error);
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Code invalide ou expiré';
          this.cdr.detectChanges();
        }
      });
    }
  }

  onResendCode() {
    if (!this.canResend) {
      console.log('⚠️ Bouton renvoyer pas encore disponible');
      return;
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    console.log('📤 Renvoi du code...');
    
    this.authService.demanderCode(this.telephone).subscribe({
      next: () => {
        console.log('✅ Code renvoyé avec succès');
        this.isLoading = false;
        this.timeLeft = 15 * 60;
        this.isExpired = false;
        this.errorMessage = '';
        this.canResend = false;
        this.resendTimeLeft = 90;
        
        if (this.timerInterval) {
          clearInterval(this.timerInterval);
        }
        if (this.resendInterval) {
          clearInterval(this.resendInterval);
        }
        this.startTimer();
        this.startResendTimer();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('❌ Erreur lors du renvoi:', error);
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de l\'envoi du code';
        this.cdr.detectChanges();
      }
    });
  }

  onChangeNumber() {
    console.log('🔄 Changement de numéro');
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
    if (this.resendInterval) {
      clearInterval(this.resendInterval);
    }
    localStorage.removeItem('temp_phone');
    this.router.navigate(['/login']);
  }

  onlyNumbers(event: KeyboardEvent): boolean {
    const charCode = event.key;
    if (!/^\d$/.test(charCode) && event.key !== 'Backspace' && event.key !== 'Delete' && event.key !== 'Tab' && event.key !== 'ArrowLeft' && event.key !== 'ArrowRight') {
      event.preventDefault();
      return false;
    }
    return true;
  }
}
