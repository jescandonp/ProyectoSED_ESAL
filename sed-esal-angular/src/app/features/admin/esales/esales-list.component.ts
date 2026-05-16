import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import {
  EsalResumen,
  PageResponse,
  EstadoEsal,
  EsalDetalle,
} from '../../../core/models/esal.model';

@Component({
  selector: 'app-admin-esales-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div>
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h2 class="sed-page-title" style="margin-bottom: 0;">ESAL — Administración</h2>
        <button class="sed-btn-primary" (click)="abrirFormularioNueva()">+ Nueva ESAL</button>
      </div>

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

      <!-- Formulario Nueva ESAL (inline) -->
      @if (mostrarFormulario()) {
        <div class="sed-card" style="margin-bottom: 16px; border-color: var(--color-primary-container);">
          <h3 style="font-size: 15px; font-weight: 600; color: var(--color-primary); margin-bottom: 16px;">
            {{ esalEditando() ? 'Editar ESAL' : 'Nueva ESAL' }}
          </h3>
          <form [formGroup]="formulario" (ngSubmit)="guardarEsal()">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px;">
              <div class="sed-field">
                <label>Nombre *</label>
                <input type="text" class="sed-input" formControlName="nombre" placeholder="Nombre de la ESAL" />
                @if (formulario.get('nombre')?.invalid && formulario.get('nombre')?.touched) {
                  <span class="sed-error-msg">El nombre es obligatorio</span>
                }
              </div>
              <div class="sed-field">
                <label>ID SIPEJ</label>
                <input type="text" class="sed-input" formControlName="idSipej" placeholder="ID SIPEJ" />
              </div>
              <div class="sed-field">
                <label>NIT</label>
                <input type="text" class="sed-input" formControlName="nit" placeholder="NIT" />
              </div>
              <div class="sed-field">
                <label>Domicilio</label>
                <input type="text" class="sed-input" formControlName="domicilio" placeholder="Domicilio" />
              </div>
              <div class="sed-field">
                <label>Correo Electrónico</label>
                <input type="email" class="sed-input" formControlName="correoElectronico" placeholder="correo@ejemplo.com" />
              </div>
              <div class="sed-field">
                <label>Término de Duración</label>
                <input type="text" class="sed-input" formControlName="terminoDuracion" placeholder="Término de duración" />
              </div>
            </div>
            <div class="sed-field" style="margin-bottom: 16px;">
              <label>Objeto Social</label>
              <textarea class="sed-input" formControlName="objetoSocial" rows="3" placeholder="Objeto social de la entidad"></textarea>
            </div>
            @if (errorFormulario()) {
              <div class="sed-error-msg" style="margin-bottom: 12px;">⚠️ {{ errorFormulario() }}</div>
            }
            <div style="display: flex; gap: 8px;">
              <button type="submit" class="sed-btn-primary" [disabled]="guardando()">
                @if (guardando()) { Guardando... } @else { Guardar }
              </button>
              <button type="button" class="sed-btn-secondary" (click)="cerrarFormulario()">Cancelar</button>
            </div>
          </form>
        </div>
      }

      <!-- Tabla -->
      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">
          <span class="spinner-lg"></span>
          <p style="margin-top: 8px;">Cargando ESALes...</p>
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
                <th>Domicilio</th>
                <th>Estado</th>
                <th>Completitud</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              @if (esales().length === 0) {
                <tr>
                  <td colspan="7" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">
                    No se encontraron ESALes
                  </td>
                </tr>
              }
              @for (esal of esales(); track esal.id) {
                <tr style="cursor: pointer;" (click)="verDetalle(esal.id)">
                  <td>{{ esal.nombre }}</td>
                  <td>{{ esal.idSipej ?? '—' }}</td>
                  <td>{{ esal.nit ?? '—' }}</td>
                  <td>{{ esal.domicilio ?? '—' }}</td>
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
                  <td>
                    <button
                      class="sed-btn-secondary"
                      style="padding: 4px 10px; font-size: 12px;"
                      (click)="$event.stopPropagation(); editarEsal(esal)"
                    >
                      Editar
                    </button>
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
  styles: [`
    .spinner-lg {
      display: inline-block;
      width: 32px;
      height: 32px;
      border: 3px solid var(--color-outline-variant);
      border-top-color: var(--color-primary-container);
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `],
})
export class AdminEsalesListComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  esales = signal<EsalResumen[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);
  paginaActual = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);

  filtroNombre = '';
  filtroEstado = '';

  mostrarFormulario = signal(false);
  esalEditando = signal<EsalResumen | null>(null);
  guardando = signal(false);
  errorFormulario = signal<string | null>(null);

  formulario: FormGroup = this.fb.group({
    nombre: [''],
    idSipej: [''],
    nit: [''],
    domicilio: [''],
    correoElectronico: [''],
    terminoDuracion: [''],
    objetoSocial: [''],
  });

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
      error: (err) => {
        this.error.set('Error al cargar las ESALes. Verifique su conexión.');
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
    this.router.navigate(['/admin/esales', id]);
  }

  abrirFormularioNueva(): void {
    this.esalEditando.set(null);
    this.formulario.reset();
    this.errorFormulario.set(null);
    this.mostrarFormulario.set(true);
  }

  editarEsal(esal: EsalResumen): void {
    this.esalEditando.set(esal);
    this.formulario.patchValue({
      nombre: esal.nombre,
      idSipej: esal.idSipej ?? '',
      nit: esal.nit ?? '',
      domicilio: esal.domicilio ?? '',
    });
    this.errorFormulario.set(null);
    this.mostrarFormulario.set(true);
  }

  cerrarFormulario(): void {
    this.mostrarFormulario.set(false);
    this.esalEditando.set(null);
    this.formulario.reset();
  }

  guardarEsal(): void {
    const nombre = this.formulario.get('nombre')?.value?.trim();
    if (!nombre) {
      this.formulario.get('nombre')?.markAsTouched();
      return;
    }

    this.guardando.set(true);
    this.errorFormulario.set(null);

    const body = this.formulario.value;
    const editando = this.esalEditando();

    const request$ = editando
      ? this.api.put<EsalResumen>(`/api/esales/${editando.id}`, body)
      : this.api.post<EsalResumen>('/api/esales', body);

    request$.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cerrarFormulario();
        this.cargar();
      },
      error: (err) => {
        this.errorFormulario.set(
          err?.error?.message ?? 'Error al guardar la ESAL.'
        );
        this.guardando.set(false);
      },
    });
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
