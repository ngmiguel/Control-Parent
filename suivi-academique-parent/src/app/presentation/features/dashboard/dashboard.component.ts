import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../../data/api/student.service';
import { StudentListItem } from '../../../core/models/student.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  enfants: StudentListItem[] = [];
  parentPhone: string | null = '';
  isLoading = true;
  
  // Modal pour téléchargement des notes
  showNotesModal = false;
  selectedStudent: StudentListItem | null = null;
  anneeAcademique: string = '';
  semestre: string = 'ANNUEL';

  constructor(
    private studentService: StudentService, 
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    // Générer l'année académique actuelle par défaut
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth();
    // Si on est entre janvier et août, on est dans l'année N-1/N
    if (currentMonth < 8) {
      this.anneeAcademique = `${currentYear - 1}-${currentYear}`;
    } else {
      this.anneeAcademique = `${currentYear}-${currentYear + 1}`;
    }
  }

  ngOnInit(): void {
    this.parentPhone = localStorage.getItem('user_phone');
    if (!this.parentPhone) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadEnfants();
  }

  loadEnfants() {
    console.log('loadEnfants appelé');
    // MODE DÉVELOPPEMENT: Données de test
    setTimeout(() => {
      console.log('Chargement des données de test');
      this.enfants = [
        {
          matricule: 'MAT001',
          nom: 'KAMGA',
          prenom: 'Jean',
          classe: 'ING3'
        },
        {
          matricule: 'MAT002',
          nom: 'KAMGA',
          prenom: 'Marie',
          classe: 'ING1'
        },
        {
          matricule: 'MAT003',
          nom: 'KAMGA',
          prenom: 'Paul',
          classe: 'ING5'
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