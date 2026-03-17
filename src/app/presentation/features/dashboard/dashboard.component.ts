import { Component, OnInit, OnDestroy, ChangeDetectorRef, AfterViewChecked } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../../data/api/student.service';
import { BulletinService } from '../../../data/api/bulletin.service';
import { EmploiDuTempsService } from '../../../data/api/emploi-du-temps.service';
import { AuthService } from '../../../data/api/auth.service';
import { NotificationService, Notification } from '../../../data/api/notification.service';
import { StudentListItem } from '../../../core/models/student.model';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, TitleCasePipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, AfterViewChecked, OnDestroy {
  enfants: StudentListItem[] = [];
  parentPhone: string | null = '';
  parentName: string = '';
  isLoading = true;
  isScrolled = false;
  currentTime: string = '';

  notifications: Notification[] = [];
  nonLuesCount = 0;
  private notifSub?: Subscription;
  private timeInterval?: ReturnType<typeof setInterval>;

  showNotifPanel = false;
  filtreNotif: string = 'TOUS';
  readonly filtresNotif = ['TOUS', 'REUNION', 'RESULTATS', 'ABSENCE', 'PAIEMENT', 'EVENEMENT'];

  get notificationsFiltrees() {
    if (this.filtreNotif === 'TOUS') return this.notifications;
    return this.notifications.filter(n => n.type === this.filtreNotif);
  }

  showNotesModal = false;
  selectedStudent: StudentListItem | null = null;
  anneeAcademique: string = '';
  semestre: string = '';
  periodesDisponibles: { anneeAcademique: string; semestre: string }[] = [];
  isLoadingPeriodes = false;

  get anneesDisponibles(): string[] {
    return [...new Set(this.periodesDisponibles.map(p => p.anneeAcademique))];
  }

  get semestresDisponibles(): string[] {
    return [...new Set(
      this.periodesDisponibles
        .filter(p => p.anneeAcademique === this.anneeAcademique)
        .map(p => p.semestre)
    )];
  }

  constructor(
    private studentService: StudentService,
    private bulletinService: BulletinService,
    private emploiDuTempsService: EmploiDuTempsService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      document.body.classList.remove('dark-mode');
      localStorage.removeItem('theme');
    }
  }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.parentPhone = localStorage.getItem('user_phone');
    this.loadEnfants();
    this.loadNotifications();
    window.addEventListener('scroll', this.onScroll.bind(this));
    this.updateTime();
    this.timeInterval = setInterval(() => this.updateTime(), 1000);
  }

  ngAfterViewChecked() {
    if (typeof (window as any).lucide !== 'undefined') {
      (window as any).lucide.createIcons();
    }
  }

  onScroll() {
    this.isScrolled = window.scrollY > 20;
    this.cdr.detectChanges();
  }

  updateTime() {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    this.cdr.detectChanges();
  }

  loadNotifications() {
    this.notificationService.getAll().subscribe({
      next: (data) => {
        this.notifications = data;
        this.nonLuesCount = data.filter(n => !n.lu).length;
        this.cdr.detectChanges();
      },
      error: () => { }
    });

    const token = localStorage.getItem('token');
    if (token) {
      this.notificationService.connecterSSE(token);
      this.notifSub = this.notificationService.notification$.subscribe(notif => {
        this.notifications.unshift(notif);
        this.nonLuesCount++;
        this.cdr.detectChanges();
      });
    }
  }

  toggleNotifPanel() {
    this.showNotifPanel = !this.showNotifPanel;
    this.cdr.detectChanges();
  }

  marquerToutesCommeLues() {
    this.notificationService.marquerToutesCommeLues().subscribe(() => {
      this.notifications.forEach(n => n.lu = true);
      this.nonLuesCount = 0;
      this.cdr.detectChanges();
    });
  }

  loadEnfants() {
    this.studentService.getMesEnfants().subscribe({
      next: (data: StudentListItem[]) => {
        this.enfants = data;
        if (data.length > 0 && data[0].nomParent) {
          this.parentName = data[0].nomParent;
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error: any) => {
        console.error('Erreur chargement enfants:', error);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getStudentPhoto(index: number): string | null {
    const photos: (string | null)[] = ['photo 1.jpg', 'photo 2.jpg', null];
    return photos[index] ?? null;
  }

  onDownloadNotes(matricule: string, nom: string) {
    const student = this.enfants.find(e => e.matricule === matricule);
    if (student) {
      this.selectedStudent = student;
      this.periodesDisponibles = [];
      this.anneeAcademique = '';
      this.semestre = '';
      this.isLoadingPeriodes = true;
      this.showNotesModal = true;
      this.cdr.detectChanges();

      this.bulletinService.getPeriodesDisponibles(matricule).subscribe({
        next: (periodes) => {
          this.periodesDisponibles = periodes;
          if (periodes.length > 0) {
            this.anneeAcademique = periodes[0].anneeAcademique;
            this.semestre = periodes[0].semestre;
          }
          this.isLoadingPeriodes = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isLoadingPeriodes = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  closeNotesModal() {
    this.showNotesModal = false;
    this.selectedStudent = null;
    this.cdr.detectChanges();
  }

  confirmDownloadNotes() {
    if (!this.selectedStudent) return;
    this.bulletinService.telechargerBulletinPDF(
      this.selectedStudent.matricule,
      this.anneeAcademique,
      this.semestre
    ).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `bulletin_${this.selectedStudent!.matricule}_${this.anneeAcademique}_${this.semestre}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.closeNotesModal();
      },
      error: (error: any) => {
        console.error('Erreur téléchargement bulletin:', error);
        alert('Erreur lors du téléchargement du bulletin.');
      }
    });
  }

  onDownloadEDT(classeId: number) {
    this.emploiDuTempsService.telechargerEmploiDuTempsPDF(classeId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Emploi_du_temps_classe_${classeId}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error: any) => {
        console.error('Erreur téléchargement EDT:', error);
        alert('Erreur lors du téléchargement de l\'emploi du temps.');
      }
    });
  }

  logout() {
    this.notificationService.deconnecter();
    this.authService.clearToken();
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  ngOnDestroy() {
    this.notifSub?.unsubscribe();
    this.notificationService.deconnecter();
    clearInterval(this.timeInterval);
  }
}
