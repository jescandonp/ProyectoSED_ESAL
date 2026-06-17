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
  readonly appVersion = 'VS 1.0.0';

  readonly navItems: NavItem[] = [
    { label: 'Inicio', route: '/dashboard', icon: 'pi pi-home', roles: ['ADMINISTRADOR', 'EXPEDIDOR'] },
    { label: 'Carga Inicial', route: '/admin/carga-inicial', icon: 'pi pi-upload', roles: ['ADMINISTRADOR'] },
    { label: 'ESAL Admin', route: '/admin/esales', icon: 'pi pi-building', roles: ['ADMINISTRADOR'] },
    { label: 'Auditoria', route: '/admin/auditoria', icon: 'pi pi-list-check', roles: ['ADMINISTRADOR'] },
    { label: 'Firmantes', route: '/admin/firmantes', icon: 'pi pi-pen-to-square', roles: ['ADMINISTRADOR'] },
    { label: 'Numeracion', route: '/admin/numeracion', icon: 'pi pi-hashtag', roles: ['ADMINISTRADOR'] },
    { label: 'Consultar ESAL', route: '/esales', icon: 'pi pi-search', roles: ['ADMINISTRADOR', 'EXPEDIDOR'] },
  ];

  readonly visibleNavItems = computed(() => {
    const rol = this.currentUser()?.rol ?? '';
    return this.navItems.filter((item) => item.roles.includes(rol));
  });

  breadcrumb(): string[] {
    const path = this.router.url.split('?')[0];

    if (path.startsWith('/admin/carga-inicial')) return ['Inicio', 'Administracion', 'Carga Inicial'];
    if (path.startsWith('/admin/esales')) return ['Inicio', 'Administracion', 'ESAL'];
    if (path.startsWith('/admin/auditoria')) return ['Inicio', 'Administracion', 'Auditoria'];
    if (path.startsWith('/admin/firmantes')) return ['Inicio', 'Administracion', 'Firmantes'];
    if (path.startsWith('/admin/numeracion')) return ['Inicio', 'Administracion', 'Numeracion'];
    if (path.startsWith('/busqueda')) return ['Inicio', 'Consulta', 'Detalle ESAL'];
    if (path.startsWith('/certificados')) return ['Inicio', 'Certificados'];
    if (path.startsWith('/esales')) return ['Inicio', 'Consulta ESAL'];

    return ['Inicio'];
  }

  pageTitle(): string {
    const items = this.breadcrumb();
    return items[items.length - 1] ?? 'Inicio';
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
