import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api.service';
import { RegisterRequest, User } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminsApi {
  constructor(private readonly api: ApiService) {}

  /** Returns UserResponse, same shape as /users/me. */
  getAll(): Observable<User[]> {
    return this.api.get<User[]>('/admins');
  }

  /** Takes RegisterRequest — the ADMIN role is assigned server-side. */
  create(request: RegisterRequest): Observable<User> {
    return this.api.post<User>('/admins', request);
  }

  delete(id: number): Observable<void> {
    return this.api.delete<void>(`/admins/${id}`);
  }
}
