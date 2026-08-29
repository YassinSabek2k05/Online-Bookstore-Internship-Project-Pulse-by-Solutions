import { Injectable, effect, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'bookstore-theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly _theme = signal<Theme>(readStoredTheme() ?? systemTheme());
  readonly theme = this._theme.asReadonly();

  constructor() {
    // styles.css keys the dark palette off :root[data-theme='dark'],
    // so writing the attribute is all it takes to switch the whole app.
    effect(() => {
      const theme = this._theme();
      document.documentElement.setAttribute('data-theme', theme);
      try {
        localStorage.setItem(STORAGE_KEY, theme);
      } catch {
        // Private browsing / blocked storage — the theme still applies for this visit.
      }
    });
  }

  toggle(): void {
    this._theme.update((theme) => (theme === 'light' ? 'dark' : 'light'));
  }
}

function readStoredTheme(): Theme | null {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === 'light' || stored === 'dark' ? stored : null;
  } catch {
    return null;
  }
}

function systemTheme(): Theme {
  // matchMedia is missing in non-browser environments (tests, SSR) — assume light.
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}
