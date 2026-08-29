/**
 * Normalized shape every ApiService method throws on failure.
 * Assumes the backend's future @RestControllerAdvice returns at least
 * { status, message } (per spec 7.7) — adjust the mapping in ApiService
 * once that handler exists if the real field names differ.
 */
export interface ApiError {
  status: number;
  message: string;
}
