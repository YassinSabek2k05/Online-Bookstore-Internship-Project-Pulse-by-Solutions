import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiError } from '../../../core/models/api-error.model';
import { AuthApi } from '../../../core/services/api/auth';
import { ThemeToggle } from '../../../shared/theme-toggle/theme-toggle';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, ThemeToggle],
  templateUrl: './login.html',
  styleUrl: '../auth.css',
})
export class Login {
  private readonly authApi = inject(AuthApi);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.authApi.login(this.form.getRawValue()).subscribe({
      next: () => {
        // TODO: role-based redirect (spec §2) needs GET /api/users/me, which the
        // backend does not expose yet — everyone lands on /home for now.
        this.router.navigateByUrl('/home');
      },
      error: (error: ApiError) => {
        this.submitting.set(false);
        this.errorMessage.set(
          error.status === 401 || error.status === 403
            ? 'Incorrect email or password.'
            : error.message,
        );
      },
    });
  }
}
