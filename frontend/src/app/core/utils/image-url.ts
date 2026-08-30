import { environment } from '../../../environments/environment';

/**
 * The backend stores imageUrl as a server-relative path ("/api/images/<key>"),
 * so it needs the origin prefixed before it can be used as an <img> src.
 * Absolute URLs pass through untouched, in case storage moves off-server later.
 */
export function resolveImageUrl(imageUrl: string | null | undefined): string | null {
  if (!imageUrl) {
    return null;
  }
  if (/^https?:\/\//i.test(imageUrl)) {
    return imageUrl;
  }
  return `${environment.serverUrl}${imageUrl.startsWith('/') ? '' : '/'}${imageUrl}`;
}
