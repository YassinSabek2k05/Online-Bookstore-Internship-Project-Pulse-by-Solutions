import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiError } from '../../../core/models/api-error.model';
import { Book } from '../../../core/models/book.model';
import { BooksApi } from '../../../core/services/api/books';
import { BookCover } from '../../../shared/book-cover/book-cover';
import { Navbar } from '../../../shared/navbar/navbar';

@Component({
  selector: 'app-home',
  imports: [CurrencyPipe, RouterLink, Navbar, BookCover],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly booksApi = inject(BooksApi);

  protected readonly books = signal<Book[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  constructor() {
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
}
