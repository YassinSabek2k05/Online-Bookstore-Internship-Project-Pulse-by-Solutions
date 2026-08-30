import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Keeps already-signed-in visitors off the public landing page. The JWT lives
 * in an httpOnly cookie the client cannot inspect, so "is it valid?" can only
 * be answered by asking the backend — a 401 means no usable session and the
 * landing page is shown as normal.
 *
 * Destination follows the same role split as login (spec §2).
 */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const landingPage = () => router.createUrlTree([auth.isAdmin() ? '/admin/books' : '/home']);

  if (auth.isAuthenticated()) {
    return landingPage();
  }

  return auth.loadCurrentUser().pipe(
    map(() => landingPage()),
    catchError(() => of(true)),
  );
};
