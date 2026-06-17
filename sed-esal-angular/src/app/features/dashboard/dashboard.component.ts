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
    icono: 'pi pi-upload',
    ruta: '/admin/carga-inicial',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Gestionar ESAL',
    descripcion: 'Crear, editar y gestionar el estado de las ESALes registradas.',
    icono: 'pi pi-building',
    ruta: '/admin/esales',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Auditoría',
    descripcion: 'Consultar el registro de eventos y acciones del sistema.',
    icono: 'pi pi-list-check',
    ruta: '/admin/auditoria',
    roles: ['ADMINISTRADOR'],
  },
  {
    titulo: 'Consultar ESAL',
    descripcion: 'Buscar y consultar el detalle de las ESALes registradas.',
    icono: 'pi pi-search',
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
      <header class="sed-page-header">
        <div>
          <span class="sed-page-kicker">Panel operativo</span>
          <h2 class="sed-page-title">Inicio</h2>
          <p class="sed-page-subtitle">
            Acceso rápido a los módulos disponibles para su rol institucional.
          </p>
        </div>
        <div class="dashboard-user sed-card">
          <i class="pi pi-user" aria-hidden="true"></i>
          <div>
            <span class="sed-meta">Usuario autenticado</span>
            <strong>{{ currentUser()?.nombre }}</strong>
            <span class="sed-chip sed-chip--activo">{{ currentUser()?.rol }}</span>
          </div>
        </div>
      </header>

      <section class="sed-section">
        <h3 class="sed-section-title">
          <i class="pi pi-th-large" aria-hidden="true"></i>
          Módulos disponibles
        </h3>
        <div class="dashboard-cards">
        @for (card of visibleCards(); track card.ruta) {
          <a [routerLink]="card.ruta" class="dashboard-card sed-card">
            <div class="dashboard-card__icon">
              <i [class]="card.icono" aria-hidden="true"></i>
            </div>
            <div class="dashboard-card__content">
              <h4 class="dashboard-card__title">{{ card.titulo }}</h4>
              <p class="dashboard-card__desc">{{ card.descripcion }}</p>
            </div>
            <span class="dashboard-card__arrow" aria-hidden="true">
              <i class="pi pi-arrow-right"></i>
            </span>
          </a>
        }
        </div>
      </section>
    </div>
  `,
  styles: [`
    .dashboard-user {
      display: flex;
      align-items: center;
      gap: var(--space-sm);
      min-width: 280px;
      padding: var(--space-sm) var(--space-md);
    }
    .dashboard-user i {
      color: var(--color-secondary);
      font-size: 22px;
    }
    .dashboard-user div {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .dashboard-user strong {
      color: var(--color-primary);
      font-size: var(--text-body-md);
    }
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
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      color: var(--color-primary-container);
      background-color: var(--color-surface-container-low);
      border-radius: var(--radius-lg);
      font-size: 18px;
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
