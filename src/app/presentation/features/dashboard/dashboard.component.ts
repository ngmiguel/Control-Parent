import { Component, OnInit, ChangeDetectorRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../../data/api/student.service';
import { BulletinService } from '../../../data/api/bulletin.service';
import { EmploiDuTempsService } from '../../../data/api/emploi-du-temps.service';
import { Student, Periode } from '../../../core/models/student.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, AfterViewChecked {
  enfants: Student[] = [];
  parentPhone: string | null = '';
  isLoading = true;
  isScrolled = false;
  isDarkMode = false;
  currentTime: string = '';
  
  // Modal pour téléchargement des notes
  showNotesModal = false;
  selectedStudent: Student | null = null;
  anneeAcademique: string = '';
  semestre: string = 'Annuel';
  anneesAcademiques: string[] = [];
  periodes: Periode[] = [];
  isLoadingPeriodes = false;

  constructor(
    private studentService: StudentService,
    private bulletinService: BulletinService,
    private emploiDuTempsService: EmploiDuTempsService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    // Charger le thème sauvegardé
    const savedTheme = localStorage.getItem('theme');
    this.isDarkMode = savedTheme === 'dark';
    if (this.isDarkMode) {
      document.body.classList.add('dark-mode');
    }
  }

  ngOnInit(): void {
    this.parentPhone = localStorage.getItem('user_phone');
    if (!this.parentPhone) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadEnfants();
    
    // Écouter le scroll pour la navbar
    window.addEventListener('scroll', this.onScroll.bind(this));
    
    // Mettre à jour l'heure
    this.updateTime();
    setInterval(() => this.updateTime(), 1000);
  }

  ngAfterViewChecked() {
    // Initialiser les icônes Lucide après chaque changement de vue
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
    this.currentTime = now.toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit'
    });
    this.cdr.detectChanges();
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    document.body.classList.toggle('dark-mode', this.isDarkMode);
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    
    // Réinitialiser les icônes après changement de thème
    setTimeout(() => {
      if (typeof (window as any).lucide !== 'undefined') {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }

  loadEnfants() {
    this.isLoading = true;
    console.log('🔄 Chargement des enfants...');
    console.log('📞 Téléphone parent:', this.parentPhone);
    
    // Appel API réel
    this.studentService.getMesEnfants().subscribe({
      next: (data: Student[]) => {
        console.log('✅ Données reçues du backend:', data);
        console.log('📊 Nombre d\'enfants:', data.length);
        this.enfants = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('❌ Erreur lors du chargement des enfants:', error);
        console.error('📄 Détails de l\'erreur:', error.error);
        console.error('🔢 Status:', error.status);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onDownloadNotes(matricule: string) {
    console.log('📄 Téléchargement des notes pour:', matricule);
    
    // Trouver l'étudiant sélectionné
    const student = this.enfants.find(e => e.matricule === matricule);
    if (student) {
      console.log('👤 Étudiant trouvé:', student);
      this.selectedStudent = student;
      this.isLoadingPeriodes = true;
      this.showNotesModal = true;
      this.cdr.detectChanges();
      
      // Charger les périodes disponibles
      console.log('🔄 Chargement des périodes pour:', matricule);
      this.bulletinService.getPeriodesDisponibles(matricule).subscribe({
        next: (periodes) => {
          console.log('✅ Périodes reçues:', periodes);
          this.periodes = periodes;
          
          // Extraire les années académiques uniques
          const anneesUniques = [...new Set(periodes.map(p => p.anneeAcademique))];
          this.anneesAcademiques = anneesUniques;
          console.log('📅 Années disponibles:', anneesUniques);
          
          // Sélectionner la première année par défaut
          if (anneesUniques.length > 0) {
            this.anneeAcademique = anneesUniques[0];
            console.log('✅ Année sélectionnée par défaut:', this.anneeAcademique);
          }
          
          this.isLoadingPeriodes = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('❌ Erreur lors du chargement des périodes:', error);
          this.isLoadingPeriodes = false;
          // Utiliser des valeurs par défaut
          const currentYear = new Date().getFullYear();
          this.anneesAcademiques = [`${currentYear}-${currentYear + 1}`];
          this.anneeAcademique = this.anneesAcademiques[0];
          console.log('⚠️ Utilisation de l\'année par défaut:', this.anneeAcademique);
          this.cdr.detectChanges();
        }
      });
    } else {
      console.error('❌ Étudiant non trouvé avec le matricule:', matricule);
    }
  }

  closeNotesModal() {
    this.showNotesModal = false;
    this.selectedStudent = null;
    this.periodes = [];
    this.cdr.detectChanges();
  }

  confirmDownloadNotes() {
    if (!this.selectedStudent) return;

    console.log('📥 Téléchargement du bulletin...');
    console.log('📋 Matricule:', this.selectedStudent.matricule);
    console.log('📅 Année:', this.anneeAcademique);
    console.log('📆 Semestre:', this.semestre);

    // Appel API réel pour télécharger le PDF
    this.bulletinService.telechargerBulletinPDF(
      this.selectedStudent.matricule,
      this.anneeAcademique,
      this.semestre
    ).subscribe({
      next: (blob: Blob) => {
        console.log('✅ PDF reçu, taille:', blob.size, 'bytes');
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `bulletin_notes_${this.selectedStudent!.matricule}_${this.anneeAcademique}_${this.semestre}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        console.log('✅ Téléchargement lancé');
        this.closeNotesModal();
      },
      error: (error) => {
        console.error('❌ Erreur lors du téléchargement du bulletin:', error);
        console.error('📄 Détails:', error.error);
        console.error('🔢 Status:', error.status);
        alert('Erreur lors du téléchargement du bulletin. Vérifiez la console pour plus de détails.');
      }
    });
  }

  onDownloadEDT(classeId: number) {
    // Appel API réel pour télécharger le PDF
    this.emploiDuTempsService.telechargerEmploiDuTempsPDF(classeId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `emploi_du_temps_classe_${classeId}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur lors du téléchargement de l\'emploi du temps:', error);
        alert('Erreur lors du téléchargement de l\'emploi du temps. Veuillez réessayer.');
      }
    });
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}