import { Component, OnInit, ChangeDetectorRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StudentService } from '../../../data/api/student.service';
import { StudentListItem } from '../../../core/models/student.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, AfterViewChecked {
  enfants: StudentListItem[] = [];
  parentPhone: string | null = '';
  isLoading = true;
  isScrolled = false;
  isDarkMode = false;
  currentTime: string = '';

  // Modal pour téléchargement des notes
  showNotesModal = false;
  selectedStudent: StudentListItem | null = null;
  anneeAcademique: string = '';
  semestre: string = 'ANNUEL';
  anneesAcademiques: string[] = [];

  constructor(
    private studentService: StudentService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    // Générer l'année académique actuelle par défaut
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth();
    // Si on est entre janvier et août, on est dans l'année N-1/N
    let baseYear: number;
    if (currentMonth < 8) {
      baseYear = currentYear - 1;
      this.anneeAcademique = `${currentYear - 1}-${currentYear}`;
    } else {
      baseYear = currentYear;
      this.anneeAcademique = `${currentYear}-${currentYear + 1}`;
    }

    // Générer les années académiques jusqu'à l'année courante (10 années avant)
    this.anneesAcademiques = [];
    for (let i = -10; i <= 0; i++) {
      const year = baseYear + i;
      this.anneesAcademiques.push(`${year}-${year + 1}`);
    }

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
    console.log('loadEnfants appelé');
    // MODE DÉVELOPPEMENT: Données de test
    setTimeout(() => {
      console.log('Chargement des données de test');
      this.enfants = [
        {
          matricule: 'MAT001',
          nom: 'Dupont',
          prenom: 'Jean',
          classe: 'Inge 4 ISI',
          photo: 'photo 1.jpg'
        },
        {
          matricule: 'MAT002',
          nom: 'Dupont',
          prenom: 'Marie',
          classe: 'Inge 4 SRT',
          photo: 'photo 2.jpg'
        }
      ];
      this.isLoading = false;
      console.log('Données chargées:', this.enfants);
      this.cdr.detectChanges();
    }, 800);

    /* MODE PRODUCTION: Décommenter pour utiliser l'API réelle
    this.studentService.getEnfantsParParent(this.parentPhone!).subscribe({
      next: (data: StudentListItem[]) => {
        this.enfants = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
    */
  }

  onDownloadNotes(matricule: string, nom: string) {
    // Trouver l'étudiant sélectionné
    const student = this.enfants.find(e => e.matricule === matricule);
    if (student) {
      this.selectedStudent = student;
      this.showNotesModal = true;
      this.cdr.detectChanges();
    }
  }

  closeNotesModal() {
    this.showNotesModal = false;
    this.selectedStudent = null;
    this.cdr.detectChanges();
  }

  confirmDownloadNotes() {
    if (!this.selectedStudent) return;

    // MODE DÉVELOPPEMENT: Simulation
    alert(`Téléchargement des notes pour ${this.selectedStudent.nom} ${this.selectedStudent.prenom}
    
Année académique: ${this.anneeAcademique}
Semestre: ${this.semestre}
Classe: ${this.selectedStudent.classe}

En mode développement, connectez votre backend pour télécharger le PDF réel.`);

    this.closeNotesModal();

    /* MODE PRODUCTION: Décommenter pour utiliser l'API réelle
    this.studentService.downloadNotes(
      this.selectedStudent.matricule, 
      this.anneeAcademique, 
      this.semestre
    ).subscribe((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Notes_${this.selectedStudent!.nom}_${this.anneeAcademique}_${this.semestre}.pdf`;
      link.click();
      this.closeNotesModal();
    });
    */
  }

  onDownloadEDT(classe: string) {
    // MODE DÉVELOPPEMENT: Simulation
    alert(`Téléchargement de l'emploi du temps pour la classe ${classe}\n\nEn mode développement, connectez votre backend pour télécharger le PDF réel.`);

    /* MODE PRODUCTION: Décommenter pour utiliser l'API réelle
    this.studentService.downloadEmploiDuTemps(classe).subscribe((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Emploi_du_temps_${classe}.pdf`;
      link.click();
    });
    */
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}