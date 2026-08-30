import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api.service';
import { Book, BookRequest } from '../../models/book.model';

@Injectable({ providedIn: 'root' })
export class BooksApi {
  constructor(private readonly api: ApiService) {}

  getAll(): Observable<Book[]> {
    return this.api.get<Book[]>('/books');
  }

  getById(id: number): Observable<Book> {
    return this.api.get<Book>(`/books/${id}`);
  }

  create(book: BookRequest): Observable<Book> {
    return this.api.post<Book>('/books', book);
  }

  update(id: number, book: BookRequest): Observable<Book> {
    return this.api.put<Book>(`/books/${id}`, book);
  }

  delete(id: number): Observable<void> {
    return this.api.delete<void>(`/books/${id}`);
  }
}
