import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { AuditoriaItem, PageResponse } from '../../../core/models/esal.model';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h2 class="sed-page-title">Auditoría</h2>

      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">
          Cargando registros de auditoría...
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
                <th>Fecha</th>
                <th>Usuario</th>
                <th>Rol</th>
                <th>Acción</th>
                <th>Entidad</th>
                <th>ID SIPEJ</th>
                <th>Resultado</th>
              </tr>
            </thead>
            <tbody>
              @if (registros().length === 0) {
                <tr>
                  <td colspan="7" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">
                    Sin registros de auditoría
                  </td>
                </tr>
              }
              @for (reg of registros(); track reg.id) {
                <tr>
                  <td style="white-space: nowrap;">{{ reg.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
                  <td>{{ reg.usuario }}</td>
                  <td>{{ reg.rol ?? '—' }}</td>
                  <td>{{ reg.accion }}</td>
                  <td>{{ reg.entidad ?? '—' }}</td>
                  <td>{{ reg.idSipej ?? '—' }}</td>
                  <td>
                    @if (reg.resultado) {
                      <span [class]="'sed-chip ' + chipResultado(reg.resultado!)">{{ reg.resultado }}</span>
                    } @else {
                      —
                    }
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
export class AuditoriaComponent implements OnInit {
  private readonly api = inject(ApiService);

  registros = signal<AuditoriaItem[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);
  paginaActual = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.api
      .get<PageResponse<AuditoriaItem>>('/api/admin/auditoria', {
        page: this.paginaActual(),
        size: 20,
      })
      .subscribe({
        next: (page) => {
          this.registros.set(page.content);
          this.totalPages.set(page.totalPages);
          this.totalElements.set(page.totalElements);
          this.cargando.set(false);
        },
        error: () => {
          this.error.set('Error al cargar los registros de auditoría.');
          this.cargando.set(false);
        },
      });
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual.set(pagina);
    this.cargar();
  }

  chipResultado(resultado: string): string {
    if (resultado === 'EXITO' || resultado === 'OK') return 'sed-chip--activo';
    if (resultado === 'ERROR') return 'sed-chip--bloqueante';
    return 'sed-chip--incompleto';
  }
}
