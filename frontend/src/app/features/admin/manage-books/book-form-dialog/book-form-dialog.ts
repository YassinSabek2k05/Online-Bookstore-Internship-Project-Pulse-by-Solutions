import { Component, computed, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiError } from '../../../../core/models/api-error.model';
import {
  BOOK_CATEGORIES,
  BOOK_CATEGORY_GROUPS,
  Book,
  BookRequest,
} from '../../../../core/models/book.model';
import { BooksApi } from '../../../../core/services/api/books';
import { ImagesApi, hasAllowedExtension } from '../../../../core/services/api/images';
import { resolveImageUrl } from '../../../../core/utils/image-url';

@Component({
  selector: 'app-book-form-dialog',
  imports: [ReactiveFormsModule],
  templateUrl: './book-form-dialog.html',
  styleUrl: '../../admin.css',
})
export class BookFormDialog {
  /** null opens the dialog in create mode. */
  readonly book = input<Book | null>(null);

  readonly saved = output<void>();
  readonly cancelled = output<void>();

  private readonly booksApi = inject(BooksApi);
  private readonly imagesApi = inject(ImagesApi);

  protected readonly saving = signal(false);
  protected readonly uploading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly uploadError = signal<string | null>(null);

  /** Holds the path returned by the image upload until the book is saved. */
  protected readonly imageUrl = signal<string | null>(null);
  protected readonly uploadedName = signal<string | null>(null);

  /**
   * Images uploaded while this dialog was open. Anything left here that the
   * saved book does not reference is deleted, so abandoning the form does not
   * leave orphans. The book's pre-existing cover is never in this set, so
   * cancelling an edit can't destroy it.
   */
  private readonly uploadedThisSession = new Set<string>();

  protected readonly previewSrc = computed(() => resolveImageUrl(this.imageUrl()));
  protected readonly isEdit = computed(() => this.book() !== null);

  protected readonly categoryGroups = BOOK_CATEGORY_GROUPS;

  /**
   * A book saved before this list existed may carry a category that isn't in
   * it; surface that value as its own option so editing doesn't wipe it.
   */
  protected readonly legacyCategory = computed(() => {
    const current = this.book()?.category;
    return current && !BOOK_CATEGORIES.includes(current) ? current : null;
  });

  protected readonly form = inject(FormBuilder).nonNullable.group({
    title: ['', [Validators.required]],
    author: ['', [Validators.required]],
    category: ['', [Validators.required]],
    price: [null as number | null, [Validators.required, Validators.min(0.01)]],
    description: [''],
  });

  constructor() {
    const existing = this.book();
    if (existing) {
      this.form.patchValue({
        title: existing.title,
        author: existing.author,
        category: existing.category,
        price: existing.price,
        description: existing.description ?? '',
      });
      this.imageUrl.set(existing.imageUrl);
    }
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = ''; // allow re-picking the same file after a failure

    if (!file) {
      return;
    }

    this.uploadError.set(null);

    // Same extensions FakeImageManager accepts — fail here rather than round-trip.
    if (!hasAllowedExtension(file.name)) {
      this.uploadError.set('Use a JPG, PNG or WEBP file.');
      return;
    }

    this.uploading.set(true);
    this.imagesApi.upload(file).subscribe({
      next: (path) => {
        this.discardIfUnused(this.imageUrl());
        this.uploadedThisSession.add(path);
        this.imageUrl.set(path);
        this.uploadedName.set(file.name);
        this.uploading.set(false);
      },
      error: (error: ApiError) => {
        this.uploadError.set(error.message);
        this.uploading.set(false);
      },
    });
  }

  protected removeImage(): void {
    this.discardIfUnused(this.imageUrl());
    this.imageUrl.set(null);
    this.uploadedName.set(null);
  }

  protected cancel(): void {
    this.discardIfUnused(this.imageUrl());
    this.cancelled.emit();
  }

  /** Deletes an image only if this dialog uploaded it and nothing kept it. */
  private discardIfUnused(url: string | null): void {
    if (!url || !this.uploadedThisSession.has(url)) {
      return;
    }
    this.uploadedThisSession.delete(url);
    // Best-effort cleanup — a failure here must not block the user.
    this.imagesApi.deleteByUrl(url).subscribe({ error: () => undefined });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: BookRequest = {
      title: value.title.trim(),
      author: value.author.trim(),
      category: value.category.trim(),
      price: Number(value.price),
      description: value.description.trim() || null,
      imageUrl: this.imageUrl(),
    };

    this.saving.set(true);
    this.errorMessage.set(null);

    const existing = this.book();
    const request$ = existing
      ? this.booksApi.update(existing.id, payload)
      : this.booksApi.create(payload);

    request$.subscribe({
      next: () => this.saved.emit(),
      error: (error: ApiError) => {
        this.saving.set(false);
        this.errorMessage.set(error.message);
      },
    });
  }
}
