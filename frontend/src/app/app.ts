import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { ThemeService } from './core/services/theme.service';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  // Instantiated here so the stored/system theme is applied on startup.
  private readonly theme = inject(ThemeService);
  private readonly auth = inject(AuthService);

  constructor() {
    // The auth cookie may still be valid from a previous visit — resolve it once
    // so the navbar shows the right state on public pages too. A 401 just means
    // nobody is signed in.
    this.auth.loadCurrentUser().subscribe({ error: () => undefined });
  }
}
