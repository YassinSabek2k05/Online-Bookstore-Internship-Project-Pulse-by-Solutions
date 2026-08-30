import { Component, inject, output, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ApiError } from '../../../../core/models/api-error.model';
import { AdminsApi } from '../../../../core/services/api/admins';

function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  return group.get('password')?.value === group.get('confirmPassword')?.value
    ? null
    : { passwordMismatch: true };
}

@Component({
  selector: 'app-admin-form-dialog',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-form-dialog.html',
  styleUrl: '../../admin.css',
})
export class AdminFormDialog {
  readonly saved = output<void>();
  readonly cancelled = output<void>();

  private readonly adminsApi = inject(AdminsApi);

  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // Mirrors RegisterRequest's constraints — the backend re-checks all of them.
  protected readonly form = inject(FormBuilder).nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{10,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch },
  );

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);

    this.adminsApi.create(this.form.getRawValue()).subscribe({
      next: () => this.saved.emit(),
      error: (error: ApiError) => {
        this.saving.set(false);
        this.errorMessage.set(
          error.status === 409 ? 'That email is already registered.' : error.message,
        );
      },
    });
  }
}
