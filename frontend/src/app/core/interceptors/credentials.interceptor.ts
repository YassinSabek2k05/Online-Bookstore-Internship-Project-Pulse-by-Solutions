import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 * The backend issues the JWT as an httpOnly cookie (see JWTFilter), so the
 * frontend never touches the token — every request to our API just needs
 * to carry cookies along with it.
 */
export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(environment.apiUrl)) {
    return next(req);
  }
  return next(req.clone({ withCredentials: true }));
};
