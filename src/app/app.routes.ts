import { Routes } from '@angular/router';
import { LoginComponent } from './presentation/features/auth/login/login.component';
import { DashboardComponent } from './presentation/features/dashboard/dashboard.component';
import { AlertsComponent } from './presentation/features/alerts/alerts.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'alerts', component: AlertsComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];