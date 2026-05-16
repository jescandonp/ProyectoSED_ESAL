import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import {
  BusquedaResultado,
  PageResponse,
  EstadoEsal,
  EstadoCompletitud,
} from '../../core/models/esal.model';

@Component({
  selector: 'app-busqueda',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <h2 class="sed-page-title">Buscar ESAL</h2>

      <!-- Filtros -->
      <div class="sed-card" style="margin-bottom: 16px;">
        <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: flex-end;">
          <div class="sed-field" style="flex: 2; min-width: 200px;">
            <label>Búsqueda general</label>
            <input
              type="text"
              class="sed-input"
              placeholder="Nombre, ID SIPEJ o NIT..."
              [(ngModel)]="filtroQ"
              (keyup.enter)="buscar()"
            />
          </div>
          <div class="sed-field" style="flex: 1; min-width: 140px;">
            <label>ID SIPEJ</label>
            <input type="text" class="sed-input" placeholder="SIPEJ exacto..." [(ngModel)]="filtroIdSipej" (keyup.enter)="buscar()" />
          </div>
          <div class="sed-field" style="flex: 1; min-width: 140px;">
            <label>NIT</label>
            <input type="text" class="sed-input" placeholder="NIT..." [(ngModel)]="filtroNit" (keyup.enter)="buscar()" />
          </div>
          <div class="sed-field" style="min-width: 150px;">
            <label>Estado</label>
            <select class="sed-input" [(ngModel)]="filtroEstado" (change)="buscar()">
              <option value="">Todos</option>
              <option value="ACTIVO">Activo</option>
              <option value="SUSPENDIDO">Suspendido</option>
              <option value="EN_LIQUIDACION">En Liquidación</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
          </div>
          <div class="sed-field" style="min-width: 180px;">
            <label>Completitud</label>
            <select class="sed-input" [(ngModel)]="filtroCompletitud" (change)="buscar()">
              <option value="">Todas</option>
              <option value="LISTO_PARA_CERTIFICAR">Listo para certificar</option>
              <option value="INCOMPLETO_NO_BLOQUEANTE">Incompleto</option>
              <option value="INCOMPLETO_BLOQUEANTE">Bloqueante</option>
            </select>
          </div>
        </div>
        <div style="display: flex; gap: 8px; margin-top: 12px;">
          <button class="sed-btn-primary" (click)="buscar()">Buscar</button>
          <button class="sed-btn-secondary" (click)="limpiar()">Limpiar</button>
        </div>
      </div>

      <!-- Resultados -->
      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">
          Buscando ESALes...
        </div>
      } @else if (error()) {
        <div class="sed-card" style="color: var(--color-error); text-align: center; padding: 24px;">
          {{ error() }}
        </div>
      } @else if (buscado()) {
        <div class="sed-card" style="padding: 0; overflow: hidden;">
          <table class="sed-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>ID SIPEJ</th>
                <th>NIT</th>
                <th>Domicilio</th>
                <th>Estado</th>
                <th>Completitud</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @if (resultados().length === 0) {
                <tr>
                  <td colspan="7" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">
                    No se encontraron ESALes con los filtros indicados.
                  </td>
                </tr>
              }
              @for (r of resultados(); track r.id) {
                <tr>
                  <td>{{ r.nombre }}</td>
                  <td>{{ r.idSipej ?? '—' }}</td>
                  <td>{{ r.nit ?? '—' }}</td>
                  <td>{{ r.domicilio ?? '—' }}</td>
                  <td>
                    <span [class]="'sed-chip ' + chipEstado(r.estado)">{{ labelEstado(r.estado) }}</span>
                  </td>
                  <td>
                    <span [class]="'sed-chip ' + chipCompletitud(r.estadoCompletitud)">{{ labelCompletitud(r.estadoCompletitud) }}</span>
                  </td>
                  <td style="white-space: nowrap;">
                    <button class="sed-btn-secondary" style="padding: 4px 10px; font-size: 12px;" (click)="verDetalle(r.id)">
                      Detalle
                    </button>
                    <button class="sed-btn-secondary" style="padding: 4px 10px; font-size: 12px; margin-left: 4px;" (click)="verPreview(r.id)">
                      Preview
                    </button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        @if (totalPages() > 1) {
          <div style="display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 16px;">
            <button class="sed-btn-secondary" [disabled]="pagina() === 0" (click)="cambiarPagina(pagina() - 1)">
              ← Anterior
            </button>
            <span style="font-size: 13px; color: var(--color-on-surface-variant);">
              Página {{ pagina() + 1 }} de {{ totalPages() }} ({{ totalElements() }} registros)
            </span>
            <button class="sed-btn-secondary" [disabled]="pagina() >= totalPages() - 1" (click)="cambiarPagina(pagina() + 1)">
              Siguiente →
            </button>
          </div>
        }
        @if (totalPages() <= 1 && buscado()) {
          <div style="font-size: 12px; color: var(--color-on-surface-variant); margin-top: 8px; text-align: right;">
            {{ totalElements() }} registro(s) encontrado(s)
          </div>
        }
      }
    </div>
  `,
})
export class BusquedaComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);

  resultados = signal<BusquedaResultado[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);
  buscado = signal(false);
  pagina = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);

  filtroQ = '';
  filtroIdSipej = '';
  filtroNit = '';
  filtroEstado = '';
  filtroCompletitud = '';

  ngOnInit(): void {
    this.cargar();
  }

  buscar(): void {
    this.pagina.set(0);
    this.cargar();
  }

  limpiar(): void {
    this.filtroQ = '';
    this.filtroIdSipej = '';
    this.filtroNit = '';
    this.filtroEstado = '';
    this.filtroCompletitud = '';
    this.buscar();
  }

  cambiarPagina(p: number): void {
    this.pagina.set(p);
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);

    const params: Record<string, string | number> = {
      page: this.pagina(),
      size: 20,
    };
    if (this.filtroQ.trim()) params['q'] = this.filtroQ.trim();
    if (this.filtroIdSipej.trim()) params['idSipej'] = this.filtroIdSipej.trim();
    if (this.filtroNit.trim()) params['nit'] = this.filtroNit.trim();
    if (this.filtroEstado) params['estado'] = this.filtroEstado;
    if (this.filtroCompletitud) params['estadoCompletitud'] = this.filtroCompletitud;

    this.api.get<PageResponse<BusquedaResultado>>('/api/busquedas/esales', params).subscribe({
      next: (page) => {
        this.resultados.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.cargando.set(false);
        this.buscado.set(true);
      },
      error: () => {
        this.error.set('Error al realizar la búsqueda. Verifique la conexión con el servidor.');
        this.cargando.set(false);
      },
    });
  }

  verDetalle(id: number): void {
    this.router.navigate(['/busqueda', id]);
  }

  verPreview(id: number): void {
    this.router.navigate(['/certificados/preview', id]);
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

  chipCompletitud(c: EstadoCompletitud): string {
    if (c === 'LISTO_PARA_CERTIFICAR') return 'sed-chip--listo';
    if (c === 'INCOMPLETO_NO_BLOQUEANTE') return 'sed-chip--incompleto';
    return 'sed-chip--bloqueante';
  }

  labelCompletitud(c: EstadoCompletitud): string {
    if (c === 'LISTO_PARA_CERTIFICAR') return 'Listo';
    if (c === 'INCOMPLETO_NO_BLOQUEANTE') return 'Incompleto';
    return 'Bloqueante';
  }
}
