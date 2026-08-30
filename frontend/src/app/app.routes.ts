import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./features/landing/landing').then((m) => m.Landing),
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'signup',
    loadComponent: () => import('./features/auth/signup/signup').then((m) => m.Signup),
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./features/customer/home/home').then((m) => m.Home),
  },
  {
    path: 'books/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/customer/book-details/book-details').then((m) => m.BookDetails),
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/admin/admin-layout/admin-layout').then((m) => m.AdminLayout),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'books' },
      {
        path: 'books',
        loadComponent: () =>
          import('./features/admin/manage-books/manage-books').then((m) => m.ManageBooks),
      },
      {
        path: 'admins',
        loadComponent: () =>
          import('./features/admin/manage-admins/manage-admins').then((m) => m.ManageAdmins),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
