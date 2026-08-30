import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiError } from '../../../core/models/api-error.model';
import { Book } from '../../../core/models/book.model';
import { BooksApi } from '../../../core/services/api/books';
import { BookCover } from '../../../shared/book-cover/book-cover';
import { Footer } from '../../../shared/footer/footer';
import { Navbar } from '../../../shared/navbar/navbar';

@Component({
  selector: 'app-book-details',
  imports: [CurrencyPipe, RouterLink, Navbar, BookCover, Footer],
  templateUrl: './book-details.html',
  styleUrl: './book-details.css',
})
export class BookDetails {
  private readonly booksApi = inject(BooksApi);
  private readonly route = inject(ActivatedRoute);

  protected readonly book = signal<Book | null>(null);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage.set('That book link looks wrong.');
      this.loading.set(false);
      return;
    }

    this.booksApi.getById(id).subscribe({
      next: (book) => {
        this.book.set(book);
        this.loading.set(false);
      },
      error: (error: ApiError) => {
        this.errorMessage.set(
          error.status === 404 ? 'We couldn’t find that book.' : error.message,
        );
        this.loading.set(false);
      },
    });
  }
}
