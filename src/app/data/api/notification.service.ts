import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

export type NotifType = 'REUNION' | 'RESULTATS' | 'ABSENCE' | 'PAIEMENT' | 'EVENEMENT';

export interface Notification {
    id: number;
    telephone: string;
    titre: string;
    message: string;
    type: NotifType;
    lu: boolean;
    dateCreation: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
    private baseUrl = 'http://localhost:8080/api/notifications';
    private eventSource: EventSource | null = null;
    private notifSubject = new Subject<Notification>();

    notification$ = this.notifSubject.asObservable();

    constructor(private http: HttpClient) { }

    getAll(): Observable<Notification[]> {
        return this.http.get<Notification[]>(this.baseUrl);
    }

    getNonLuesCount(): Observable<{ count: number }> {
        return this.http.get<{ count: number }>(`${this.baseUrl}/non-lues`);
    }

    marquerToutesCommeLues(): Observable<void> {
        return this.http.put<void>(`${this.baseUrl}/lire-tout`, {});
    }

    connecterSSE(token: string): void {
        if (this.eventSource) return;

        const connect = () => {
            this.eventSource = new EventSource(
                `${this.baseUrl}/stream?token=${token}`
            );

            this.eventSource.addEventListener('notification', (event: MessageEvent) => {
                const notif: Notification = JSON.parse(event.data);
                this.notifSubject.next(notif);
            });

            this.eventSource.onerror = () => {
                this.eventSource?.close();
                this.eventSource = null;
                // Retry après 5 secondes
                setTimeout(() => connect(), 5000);
            };
        };

        connect();
    }

    deconnecter(): void {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
    }
}
