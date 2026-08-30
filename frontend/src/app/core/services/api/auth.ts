import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api.service';
import { LoginRequest, RegisterRequest } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthApi {
  constructor(private readonly api: ApiService) {}

  /** Both endpoints return an empty body; login's response sets the auth cookie. */
  login(request: LoginRequest): Observable<void> {
    return this.api.post<void>('/auth/login', request);
  }

  register(request: RegisterRequest): Observable<void> {
    return this.api.post<void>('/auth/register', request);
  }

  /** Expires the auth cookie server-side; the client cannot clear it itself. */
  logout(): Observable<void> {
    return this.api.post<void>('/auth/logout', {});
  }
}
