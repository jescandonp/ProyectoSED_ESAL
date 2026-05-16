import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles: string[];
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly currentUser = this.auth.currentUser;

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: '📊', roles: ['ADMINISTRADOR', 'EXPEDIDOR'] },
    { label: 'Carga Inicial', route: '/admin/carga-inicial', icon: '📤', roles: ['ADMINISTRADOR'] },
    { label: 'ESAL (Admin)', route: '/admin/esales', icon: '🏢', roles: ['ADMINISTRADOR'] },
    { label: 'Auditoría', route: '/admin/auditoria', icon: '📋', roles: ['ADMINISTRADOR'] },
    { label: 'Consultar ESAL', route: '/esales', icon: '🔍', roles: ['ADMINISTRADOR', 'EXPEDIDOR'] },
    { label: 'Buscar ESAL', route: '/busqueda', icon: '🔎', roles: ['ADMINISTRADOR', 'EXPEDIDOR'] },
  ];

  readonly visibleNavItems = computed(() => {
    const rol = this.currentUser()?.rol ?? '';
    return this.navItems.filter((item) => item.roles.includes(rol));
  });

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
