import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Footer } from '../../shared/footer/footer';
import { Navbar } from '../../shared/navbar/navbar';

/** Public welcome page. Deliberately fetches nothing — the shelf is behind login. */
@Component({
  selector: 'app-landing',
  imports: [RouterLink, Navbar, Footer],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {
  protected readonly decorativeCovers = [
    { title: 'Jane Eyre', author: 'Charlotte Brontë', color: '#4F5B36' },
    { title: 'Moby-Dick', author: 'Herman Melville', color: '#8C4A2F' },
    { title: 'Frankenstein', author: 'Mary Shelley', color: '#2C3A55' },
    { title: 'Dracula', author: 'Bram Stoker', color: '#6B2C35' },
  ];
}
