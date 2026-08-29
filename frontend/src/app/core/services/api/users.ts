// core/services/api/users.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api.service';
import { User } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsersApi {
  constructor(private readonly api: ApiService) {}

  getMe(): Observable<User> {
    return this.api.get<User>('/users/me');
  }
}
