import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api.service';

/** Mirrors FakeImageManager.ALLOWED_EXTENSIONS. */
export const ALLOWED_IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.webp'] as const;

@Injectable({ providedIn: 'root' })
export class ImagesApi {
  constructor(private readonly api: ApiService) {}

  /**
   * Stores the file and resolves to its server-relative path
   * ("/api/images/<key>"), which then becomes a book's imageUrl.
   */
  upload(file: File): Observable<string> {
    const form = new FormData();
    form.append('file', file);
    return this.api.postFormForText('/images', form);
  }

  /** Takes the stored path ("/api/images/<key>"), not the bare key. */
  deleteByUrl(imageUrl: string): Observable<void> {
    return this.api.delete<void>(`/images/${imageUrl.split('/').pop()}`);
  }
}

export function hasAllowedExtension(filename: string): boolean {
  const lower = filename.toLowerCase();
  return ALLOWED_IMAGE_EXTENSIONS.some((extension) => lower.endsWith(extension));
}
