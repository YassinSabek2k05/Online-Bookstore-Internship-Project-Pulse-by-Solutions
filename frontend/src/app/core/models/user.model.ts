// core/models/user.model.ts
export type Role = 'USER' | 'ADMIN';

export interface User {
  id: number;
  email: string;
  phone: string;
  role: Role;
}

export interface LoginRequest {
  email: string;
  password: string;
}

// The backend re-checks confirmPassword, so it travels with the request.
export interface RegisterRequest {
  email: string;
  phone: string;
  password: string;
  confirmPassword: string;
}
