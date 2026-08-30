import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Keeps a signed-in USER out of the admin UI. Cosmetic only — SecurityConfig
 * locks /api/admins and the book mutations to hasRole("ADMIN") regardless.
 */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const decide = () => (auth.isAdmin() ? true : router.createUrlTree(['/home']));

  if (auth.isAuthenticated()) {
    return decide();
  }

  return auth.loadCurrentUser().pipe(
    map(decide),
    catchError(() => of(router.createUrlTree(['/login']))),
  );
};
