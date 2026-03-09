import { Component, OnInit, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

interface Alert {
    id: string;
    type: 'reunion' | 'absence' | 'notes' | 'sortie' | 'modification';
    category: 'toutes' | 'academique' | 'absences';
    title: string;
    description: string;
    time: string;
    actionLabel?: string;
    actionLink?: string;
    icon: string;
    bgColor: string;
}

@Component({
    selector: 'app-alerts',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './alerts.html',
    styleUrl: './alerts.scss'
})
export class AlertsComponent implements OnInit, AfterViewChecked {
    parentPhone: string | null = '';
    activeTab: 'toutes' | 'academique' | 'absences' = 'toutes';

    allAlerts: Alert[] = [
        {
            id: '1',
            type: 'reunion',
            category: 'academique',
            title: "Réunion de parents d'élèves",
            description: "Invitation à la réunion trimestrielle le vendredi 25 octobre à 18h en salle polyvalente.",
            time: 'Il y a 2h',
            actionLabel: 'Confirmer présence',
            icon: 'users',
            bgColor: '#eff6ff'
        },
        {
            id: '2',
            type: 'absence',
            category: 'absences',
            title: 'Absence signalée',
            description: "Une absence injustifiée a été enregistrée pour le cours de Mathématiques de 08:00 (M. Dupont).",
            time: 'Ce matin',
            actionLabel: "Justifier l'absence",
            icon: 'alert-circle',
            bgColor: '#fef2f2'
        },
        {
            id: '3',
            type: 'notes',
            category: 'academique',
            title: 'Publication des notes',
            description: "Les résultats du contrôle de Physique-Chimie du 15 octobre sont désormais disponibles sur votre espace.",
            time: 'Hier',
            actionLabel: 'Voir les notes',
            icon: 'star',
            bgColor: '#fffbeb'
        },
        {
            id: '4',
            type: 'sortie',
            category: 'toutes',
            title: 'Sortie scolaire',
            description: "Rappel : La sortie au Musée National des Arts est prévue pour ce jeudi. Veuillez vérifier l'heure de départ.",
            time: 'Il y a 2 jours',
            actionLabel: 'Informations',
            icon: 'bus',
            bgColor: '#f0fdf4'
        },
        {
            id: '5',
            type: 'modification',
            category: 'academique',
            title: "Modification d'emploi du temps",
            description: "Le cours de Français est déplacé au lundi 14:00.",
            time: '12 oct.',
            icon: 'clock',
            bgColor: '#f9fafb'
        }
    ];

    constructor(
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.parentPhone = localStorage.getItem('user_phone');
        if (!this.parentPhone) {
            this.router.navigate(['/login']);
            return;
        }
    }

    ngAfterViewChecked() {
        if (typeof (window as any).lucide !== 'undefined') {
            (window as any).lucide.createIcons();
        }
    }

    get filteredAlerts(): Alert[] {
        if (this.activeTab === 'toutes') {
            return this.allAlerts;
        }
        return this.allAlerts.filter(alert => alert.category === this.activeTab);
    }

    get olderAlerts(): Alert[] {
        return this.allAlerts.slice(4);
    }

    setActiveTab(tab: 'toutes' | 'academique' | 'absences') {
        this.activeTab = tab;
        this.cdr.detectChanges();
    }

    onAlertAction(alert: Alert) {
        console.log('Action sur alerte:', alert);
        // Implémenter les actions selon le type d'alerte
    }

    goBack() {
        this.router.navigate(['/dashboard']);
    }

    logout() {
        localStorage.clear();
        this.router.navigate(['/login']);
    }
}
