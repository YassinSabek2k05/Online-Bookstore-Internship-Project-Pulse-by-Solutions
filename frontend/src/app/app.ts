import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
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

  // The session is resolved by the route guards — every page that shows a
  // navbar sits behind one, so there is no need to probe /users/me here too.
}
