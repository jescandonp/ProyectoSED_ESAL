export type UserRole = 'ADMINISTRADOR' | 'EXPEDIDOR';

export interface User {
  email: string;
  nombre: string;
  rol: UserRole;
}
