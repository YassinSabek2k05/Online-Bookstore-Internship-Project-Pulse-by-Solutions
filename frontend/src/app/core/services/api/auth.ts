import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest, RegisterRequest } from '../../models/user.model';
import { ApiService } from '../api.service';

@Injectable({ providedIn: 'root' })
export class AuthApi {
  private readonly api = inject(ApiService);

  /** Both endpoints return an empty body; login's response sets the auth cookie. */
  login(request: LoginRequest): Observable<void> {
    return this.api.post<void>('/auth/login', request);
  }

  register(request: RegisterRequest): Observable<void> {
    return this.api.post<void>('/auth/register', request);
  }
}
