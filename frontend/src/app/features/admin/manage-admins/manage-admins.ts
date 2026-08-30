import { Component, inject, signal } from '@angular/core';
import { ApiError } from '../../../core/models/api-error.model';
import { User } from '../../../core/models/user.model';
import { AdminsApi } from '../../../core/services/api/admins';
import { AuthService } from '../../../core/services/auth.service';
import { ConfirmDialog } from '../../../shared/confirm-dialog/confirm-dialog';
import { AdminFormDialog } from './admin-form-dialog/admin-form-dialog';

@Component({
  selector: 'app-manage-admins',
  imports: [AdminFormDialog, ConfirmDialog],
  templateUrl: './manage-admins.html',
  styleUrl: '../admin.css',
})
export class ManageAdmins {
  private readonly adminsApi = inject(AdminsApi);
  private readonly auth = inject(AuthService);

  protected readonly admins = signal<User[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly dialogOpen = signal(false);
  protected readonly pendingDelete = signal<User | null>(null);
  protected readonly deleting = signal(false);

  /** The backend rejects self-deletion; this hides the button to match. */
  protected readonly currentUserId = this.auth.currentUser;

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.adminsApi.getAll().subscribe({
      next: (admins) => {
        this.admins.set(admins);
        this.loading.set(false);
      },
      error: (error: ApiError) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }

  protected onSaved(): void {
    this.dialogOpen.set(false);
    this.load();
  }

  protected confirmDelete(): void {
    const admin = this.pendingDelete();
    if (!admin) {
      return;
    }

    this.deleting.set(true);
    this.adminsApi.delete(admin.id).subscribe({
      next: () => {
        this.admins.update((admins) => admins.filter((a) => a.id !== admin.id));
        this.deleting.set(false);
        this.pendingDelete.set(null);
      },
      error: (error: ApiError) => {
        this.errorMessage.set(error.message);
        this.deleting.set(false);
        this.pendingDelete.set(null);
      },
    });
  }
}
