import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../data/api/auth.service';

export const authGuard = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    // Vérifier que le token ET le numéro de téléphone sont présents
    const session = authService.getSessionInfo();

    if (session.token && session.phone) {
        return true;
    }

    // Nettoyer la session incomplète
    authService.clearToken();
    authService.clearPhoneNumber();

    router.navigate(['/auth/login']);
    return false;
};