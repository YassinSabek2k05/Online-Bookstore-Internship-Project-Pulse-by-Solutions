import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
    <footer class="footer">
      <div class="footer-inner">
        <span class="mark">BOOK STORE</span>
        <span class="copy">© {{ year }}</span>
      </div>
    </footer>
  `,
  styles: `
    .footer {
      border-top: 1px solid var(--border);
      background: var(--bg);
    }

    .footer-inner {
      max-width: 1200px;
      margin: 0 auto;
      padding: 26px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
    }

    .mark {
      font-family: var(--font-display);
      font-weight: 700;
      font-size: 13px;
      letter-spacing: 0.16em;
      color: var(--text-muted);
    }

    .copy {
      font-size: 13px;
      color: var(--text-muted);
    }
  `,
})
export class Footer {
  protected readonly year = new Date().getFullYear();
}
