import { Component, computed, input } from '@angular/core';
import { Book } from '../../core/models/book.model';
import { resolveImageUrl } from '../../core/utils/image-url';

/** Books may have no imageUrl, so the fallback is a typographic cover. */
const FALLBACK_COLORS = [
  '#2E4B4A',
  '#6B2C35',
  '#8C4A2F',
  '#2C3A55',
  '#4F5B36',
  '#4A2E44',
  '#B8873A',
  '#445055',
];

@Component({
  selector: 'app-book-cover',
  templateUrl: './book-cover.html',
  styleUrl: './book-cover.css',
})
export class BookCover {
  readonly book = input.required<Book>();

  protected readonly imageSrc = computed(() => resolveImageUrl(this.book().imageUrl));

  /** Keyed off the id so a book keeps the same colour between renders. */
  protected readonly fallbackColor = computed(
    () => FALLBACK_COLORS[this.book().id % FALLBACK_COLORS.length],
  );
}
