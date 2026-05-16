import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { EsalResumen, PageResponse, EstadoEsal } from '../../core/models/esal.model';

@Component({
  selector: 'app-esales-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <h2 class="sed-page-title">Consultar ESAL</h2>

      <!-- Filtros -->
      <div class="sed-card" style="margin-bottom: 16px;">
        <div style="display: flex; gap: 16px; flex-wrap: wrap; align-items: flex-end;">
          <div class="sed-field" style="flex: 1; min-width: 200px;">
            <label>Nombre</label>
            <input
              type="text"
              class="sed-input"
              placeholder="Buscar por nombre..."
              [(ngModel)]="filtroNombre"
              (keyup.enter)="buscar()"
            />
          </div>
          <div class="sed-field" style="min-width: 160px;">
            <label>Estado</label>
            <select class="sed-input" [(ngModel)]="filtroEstado" (change)="buscar()">
              <option value="">Todos</option>
              <option value="ACTIVO">Activo</option>
              <option value="SUSPENDIDO">Suspendido</option>
              <option value="EN_LIQUIDACION">En Liquidación</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
          </div>
          <button class="sed-btn-primary" (click)="buscar()">Buscar</button>
          <button class="sed-btn-secondary" (click)="limpiarFiltros()">Limpiar</button>
        </div>
      </div>

      <!-- Tabla -->
      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">
          Cargando ESALes...
        </div>
      } @else if (error()) {
        <div class="sed-card" style="color: var(--color-error); text-align: center; padding: 24px;">
          ⚠️ {{ error() }}
        </div>
      } @else {
        <div class="sed-card" style="padding: 0; overflow: hidden;">
          <table class="sed-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>ID SIPEJ</th>
                <th>NIT</th>
                <th>Estado</th>
                <th>Completitud</th>
              </tr>
            </thead>
            <tbody>
              @if (esales().length === 0) {
                <tr>
                  <td colspan="5" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">
                    No se encontraron ESALes
                  </td>
                </tr>
              }
              @for (esal of esales(); track esal.id) {
                <tr style="cursor: pointer;" (click)="verDetalle(esal.id)">
                  <td>{{ esal.nombre }}</td>
                  <td>{{ esal.idSipej ?? '—' }}</td>
                  <td>{{ esal.nit ?? '—' }}</td>
                  <td>
                    <span [class]="'sed-chip ' + chipEstado(esal.estado)">
                      {{ labelEstado(esal.estado) }}
                    </span>
                  </td>
                  <td>
                    <span [class]="'sed-chip ' + chipCompletitud(esal.estadoCompletitud)">
                      {{ labelCompletitud(esal.estadoCompletitud) }}
                    </span>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Paginación -->
        @if (totalPages() > 1) {
          <div style="display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 16px;">
            <button
              class="sed-btn-secondary"
              [disabled]="paginaActual() === 0"
              (click)="cambiarPagina(paginaActual() - 1)"
            >
              ← Anterior
            </button>
            <span style="font-size: 13px; color: var(--color-on-surface-variant);">
              Página {{ paginaActual() + 1 }} de {{ totalPages() }}
              ({{ totalElements() }} registros)
            </span>
            <button
              class="sed-btn-secondary"
              [disabled]="paginaActual() >= totalPages() - 1"
              (click)="cambiarPagina(paginaActual() + 1)"
            >
              Siguiente →
            </button>
          </div>
        }
      }
    </div>
  `,
})
export class EsalesListComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  esales = signal<EsalResumen[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);
  paginaActual = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);

  filtroNombre = '';
  filtroEstado = '';

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);

    const params: Record<string, string | number> = {
      page: this.paginaActual(),
      size: 20,
    };
    if (this.filtroNombre.trim()) params['nombre'] = this.filtroNombre.trim();
    if (this.filtroEstado) params['estado'] = this.filtroEstado;

    this.api.get<PageResponse<EsalResumen>>('/api/esales', params).subscribe({
      next: (page) => {
        this.esales.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('Error al cargar las ESALes.');
        this.cargando.set(false);
      },
    });
  }

  buscar(): void {
    this.paginaActual.set(0);
    this.cargar();
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = '';
    this.buscar();
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual.set(pagina);
    this.cargar();
  }

  verDetalle(id: number): void {
    this.router.navigate(['/esales', id]);
  }

  chipEstado(estado: EstadoEsal): string {
    const map: Record<EstadoEsal, string> = {
      ACTIVO: 'sed-chip--activo',
      SUSPENDIDO: 'sed-chip--suspendido',
      EN_LIQUIDACION: 'sed-chip--liquidacion',
      CANCELADO: 'sed-chip--cancelado',
    };
    return map[estado] ?? '';
  }

  labelEstado(estado: EstadoEsal): string {
    const map: Record<EstadoEsal, string> = {
      ACTIVO: 'Activo',
      SUSPENDIDO: 'Suspendido',
      EN_LIQUIDACION: 'En Liquidación',
      CANCELADO: 'Cancelado',
    };
    return map[estado] ?? estado;
  }

  chipCompletitud(c: string): string {
    if (c === 'LISTO_PARA_CERTIFICAR') return 'sed-chip--listo';
    if (c === 'INCOMPLETO_NO_BLOQUEANTE') return 'sed-chip--incompleto';
    return 'sed-chip--bloqueante';
  }

  labelCompletitud(c: string): string {
    if (c === 'LISTO_PARA_CERTIFICAR') return 'Listo';
    if (c === 'INCOMPLETO_NO_BLOQUEANTE') return 'Incompleto';
    return 'Bloqueante';
  }
}
