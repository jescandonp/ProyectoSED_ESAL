import { Injectable, signal, computed } from '@angular/core';
import { User, UserRole } from '../models/user.model';

interface LocalDevUser {
  email: string;
  password: string;
  nombre: string;
  rol: UserRole;
}

const LOCAL_DEV_USERS: LocalDevUser[] = [
  {
    email: 'admin@educacionbogota.edu.co',
    password: 'admin123',
    nombre: 'Administrador SED',
    rol: 'ADMINISTRADOR',
  },
  {
    email: 'expedidor@educacionbogota.edu.co',
    password: 'expedidor123',
    nombre: 'Expedidor SED',
    rol: 'EXPEDIDOR',
  },
];

const STORAGE_KEY = 'sed_esal_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _currentUser = signal<User | null>(this._loadFromStorage());

  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.rol === 'ADMINISTRADOR');

  login(email: string, password: string): boolean {
    const found = LOCAL_DEV_USERS.find(
      (u) => u.email === email && u.password === password
    );
    if (!found) {
      return false;
    }
    const user: User = { email: found.email, nombre: found.nombre, rol: found.rol };
    this._currentUser.set(user);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return true;
  }

  logout(): void {
    this._currentUser.set(null);
    localStorage.removeItem(STORAGE_KEY);
  }

  private _loadFromStorage(): User | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? (JSON.parse(raw) as User) : null;
    } catch {
      return null;
    }
  }
}
