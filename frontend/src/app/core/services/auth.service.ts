import { Injectable, computed, signal } from '@angular/core';
import { Observable, switchMap, tap } from 'rxjs';
import { LoginRequest, User } from '../models/user.model';
import { AuthApi } from './api/auth';
import { UsersApi } from './api/users';

/**
 * Holds the signed-in user for the UI. The JWT itself lives in an httpOnly
 * cookie the client cannot read, so /users/me is the only way to learn who
 * the session belongs to — and the backend stays the real authority.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _currentUser = signal<User | null>(null);
  readonly currentUser = this._currentUser.asReadonly();

  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  /** "yassin@example.com" -> "yassin", for the navbar greeting. */
  readonly displayName = computed(() => this._currentUser()?.email.split('@')[0] ?? '');

  constructor(
    private readonly authApi: AuthApi,
    private readonly usersApi: UsersApi,
  ) {}

  login(request: LoginRequest): Observable<User> {
    // Login sets the cookie but returns no body, so the profile fetch that
    // follows is what tells us the role to redirect on.
    return this.authApi.login(request).pipe(switchMap(() => this.loadCurrentUser()));
  }

  loadCurrentUser(): Observable<User> {
    return this.usersApi.getMe().pipe(tap((user) => this._currentUser.set(user)));
  }

  /**
   * Clears local state immediately so the UI never looks signed in, then asks
   * the backend to expire the cookie. A failed call still leaves the user
   * logged out here — the cookie expires on its own soon enough.
   */
  logout(): Observable<void> {
    this._currentUser.set(null);
    return this.authApi.logout();
  }
}
