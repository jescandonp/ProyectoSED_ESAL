import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

interface DashboardCard {
  titulo: string;
  descripcion: string;
  icono: string;
  ruta: string;
  roles: string[];
}

const CARDS: DashboardCard[] = [
  {
    titulo: 'Carga Inicial',
    descripcion: 'Importar base histórica de ESALes desde Excel e inicializar diccionario.',
    icono: '📤',
    ruta: '/admin/carga-inicial',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Gestionar ESAL',
    descripcion: 'Crear, editar y gestionar el estado de las ESALes registradas.',
    icono: '🏢',
    ruta: '/admin/esales',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Auditoría',
    descripcion: 'Consultar el registro de eventos y acciones del sistema.',
    icono: '📋',
    ruta: '/admin/auditoria',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Consultar ESAL',
    descripcion: 'Buscar y consultar el detalle de las ESALes registradas.',
    icono: '🔍',
    ruta: '/esales',
    roles: ['ADMINISTRADOR', 'EXPEDIDOR'],
  },
];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div>
      <h2 class="sed-page-title">Dashboard</h2>

      <div class="dashboard-welcome sed-card" style="margin-bottom: 24px;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <span style="font-size: 32px;">👤</span>
          <div>
            <p style="font-size: 18px; font-weight: 600; color: var(--color-primary);">
              Bienvenido/a, {{ currentUser()?.nombre }}
            </p>
            <p style="margin-top: 4px; color: var(--color-on-surface-variant); font-size: 13px;">
              Rol: <strong>{{ currentUser()?.rol }}</strong>
            </p>
          </div>
        </div>
      </div>

      <h3 style="font-size: 16px; font-weight: 600; color: var(--color-on-surface-variant); margin-bottom: 16px;">
        Acceso rápido
      </h3>

      <div class="dashboard-cards">
        @for (card of visibleCards(); track card.ruta) {
          <a [routerLink]="card.ruta" class="dashboard-card sed-card">
            <div class="dashboard-card__icon">{{ card.icono }}</div>
            <div class="dashboard-card__content">
              <h4 class="dashboard-card__title">{{ card.titulo }}</h4>
              <p class="dashboard-card__desc">{{ card.descripcion }}</p>
            </div>
            <span class="dashboard-card__arrow">→</span>
          </a>
        }
      </div>
    </div>
  `,
  styles: [`
    .dashboard-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px;
    }
    .dashboard-card {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      text-decoration: none;
      color: inherit;
      transition: box-shadow 0.2s ease, border-color 0.2s ease;
      cursor: pointer;
    }
    .dashboard-card:hover {
      border-color: var(--color-primary-container);
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      text-decoration: none;
    }
    .dashboard-card__icon {
      font-size: 28px;
      flex-shrink: 0;
    }
    .dashboard-card__content {
      flex: 1;
    }
    .dashboard-card__title {
      font-size: 15px;
      font-weight: 600;
      color: var(--color-primary-container);
      margin-bottom: 4px;
    }
    .dashboard-card__desc {
      font-size: 13px;
      color: var(--color-on-surface-variant);
      line-height: 1.4;
    }
    .dashboard-card__arrow {
      font-size: 18px;
      color: var(--color-primary-container);
      flex-shrink: 0;
    }
  `],
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
  readonly currentUser = this.auth.currentUser;

  readonly visibleCards = computed(() => {
    const rol = this.currentUser()?.rol ?? '';
    return CARDS.filter((c) => c.roles.includes(rol));
  });
}
