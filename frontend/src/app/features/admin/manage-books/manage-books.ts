import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ApiError } from '../../../core/models/api-error.model';
import { Book } from '../../../core/models/book.model';
import { BooksApi } from '../../../core/services/api/books';
import { resolveImageUrl } from '../../../core/utils/image-url';
import { ConfirmDialog } from '../../../shared/confirm-dialog/confirm-dialog';
import { BookFormDialog } from './book-form-dialog/book-form-dialog';

@Component({
  selector: 'app-manage-books',
  imports: [CurrencyPipe, BookFormDialog, ConfirmDialog],
  templateUrl: './manage-books.html',
  styleUrl: '../admin.css',
})
export class ManageBooks {
  private readonly booksApi = inject(BooksApi);

  protected readonly books = signal<Book[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  /** null = closed, undefined-book = create, book = edit. */
  protected readonly dialogOpen = signal(false);
  protected readonly editing = signal<Book | null>(null);

  /** The book awaiting delete confirmation, if any. */
  protected readonly pendingDelete = signal<Book | null>(null);
  protected readonly deleting = signal(false);

  protected readonly thumbSrc = resolveImageUrl;

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.booksApi.getAll().subscribe({
      next: (books) => {
        this.books.set(books);
        this.loading.set(false);
      },
      error: (error: ApiError) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }

  protected openCreate(): void {
    this.editing.set(null);
    this.dialogOpen.set(true);
  }

  protected openEdit(book: Book): void {
    this.editing.set(book);
    this.dialogOpen.set(true);
  }

  protected closeDialog(): void {
    this.dialogOpen.set(false);
  }

  protected onSaved(): void {
    this.dialogOpen.set(false);
    this.load();
  }

  protected confirmDelete(): void {
    const book = this.pendingDelete();
    if (!book) {
      return;
    }

    this.deleting.set(true);
    this.booksApi.delete(book.id).subscribe({
      next: () => {
        this.books.update((books) => books.filter((b) => b.id !== book.id));
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
