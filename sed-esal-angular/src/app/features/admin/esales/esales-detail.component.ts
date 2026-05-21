import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import {
  EsalDetalle,
  CompletitudResponse,
  DocumentoSoporte,
  EstadoEsal,
} from '../../../core/models/esal.model';

type TabActiva = 'info' | 'estado' | 'completitud' | 'documentos';

@Component({
  selector: 'app-admin-esales-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div>
      <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
        <button class="sed-btn-secondary" style="padding: 6px 12px;" (click)="volver()">← Volver</button>
        <h2 class="sed-page-title" style="margin-bottom: 0;">Detalle ESAL</h2>
      </div>

      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">
          Cargando...
        </div>
      } @else if (error()) {
        <div class="sed-card" style="color: var(--color-error); text-align: center; padding: 24px;">
          ⚠️ {{ error() }}
        </div>
      } @else if (esal()) {
        <!-- Header con nombre y estado -->
        <div class="sed-card" style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
          <div>
            <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary);">{{ esal()!.nombre }}</h3>
            <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-top: 4px;">
              ID SIPEJ: {{ esal()!.idSipej ?? '—' }} | NIT: {{ esal()!.nit ?? '—' }}
            </p>
          </div>
          <div style="display: flex; gap: 8px; align-items: center;">
            <button class="sed-btn-primary" style="padding: 6px 12px;" (click)="irAMantenimiento()">Actualizar información</button>
            <span [class]="'sed-chip ' + chipEstado(esal()!.estado)">{{ labelEstado(esal()!.estado) }}</span>
            <span [class]="'sed-chip ' + chipCompletitud(esal()!.estadoCompletitud)">{{ labelCompletitud(esal()!.estadoCompletitud) }}</span>
          </div>
        </div>

        <!-- Tabs -->
        <div class="tabs-nav" style="margin-bottom: 16px;">
          <button [class]="'tab-btn' + (tabActiva() === 'info' ? ' tab-btn--active' : '')" (click)="tabActiva.set('info')">
            📋 Información
          </button>
          <button [class]="'tab-btn' + (tabActiva() === 'estado' ? ' tab-btn--active' : '')" (click)="tabActiva.set('estado')">
            🔄 Estado
          </button>
          <button [class]="'tab-btn' + (tabActiva() === 'completitud' ? ' tab-btn--active' : '')" (click)="cargarCompletitud(); tabActiva.set('completitud')">
            🚦 Completitud
          </button>
          <button [class]="'tab-btn' + (tabActiva() === 'documentos' ? ' tab-btn--active' : '')" (click)="cargarDocumentos(); tabActiva.set('documentos')">
            📄 Documentos
          </button>
        </div>

        <!-- Tab: Información Principal -->
        @if (tabActiva() === 'info') {
          <div class="sed-card">
            @if (!editandoInfo()) {
              <div style="display: flex; justify-content: flex-end; margin-bottom: 16px;">
                <button class="sed-btn-primary" (click)="iniciarEdicion()">✏️ Editar</button>
              </div>
              <div class="info-grid">
                <div class="info-item"><span class="info-label">Nombre</span><span class="info-valor">{{ esal()!.nombre }}</span></div>
                <div class="info-item"><span class="info-label">ID SIPEJ</span><span class="info-valor">{{ esal()!.idSipej ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">NIT</span><span class="info-valor">{{ esal()!.nit ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Domicilio</span><span class="info-valor">{{ esal()!.domicilio ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Correo Electrónico</span><span class="info-valor">{{ esal()!.correoElectronico ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Término de Duración</span><span class="info-valor">{{ esal()!.terminoDuracion ?? '—' }}</span></div>
                <div class="info-item info-item--full"><span class="info-label">Objeto Social</span><span class="info-valor">{{ esal()!.objetoSocial ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Creado por</span><span class="info-valor">{{ esal()!.createdBy ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Creado el</span><span class="info-valor">{{ esal()!.createdAt | date:'dd/MM/yyyy HH:mm' }}</span></div>
                @if (esal()!.updatedAt) {
                  <div class="info-item"><span class="info-label">Actualizado por</span><span class="info-valor">{{ esal()!.updatedBy ?? '—' }}</span></div>
                  <div class="info-item"><span class="info-label">Actualizado el</span><span class="info-valor">{{ esal()!.updatedAt | date:'dd/MM/yyyy HH:mm' }}</span></div>
                }
              </div>
            } @else {
              <form [formGroup]="formularioEdicion" (ngSubmit)="guardarEdicion()">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px;">
                  <div class="sed-field">
                    <label>Nombre *</label>
                    <input type="text" class="sed-input" formControlName="nombre" />
                  </div>
                  <div class="sed-field">
                    <label>ID SIPEJ</label>
                    <input type="text" class="sed-input" formControlName="idSipej" />
                  </div>
                  <div class="sed-field">
                    <label>NIT</label>
                    <input type="text" class="sed-input" formControlName="nit" />
                  </div>
                  <div class="sed-field">
                    <label>Domicilio</label>
                    <input type="text" class="sed-input" formControlName="domicilio" />
                  </div>
                  <div class="sed-field">
                    <label>Correo Electrónico</label>
                    <input type="email" class="sed-input" formControlName="correoElectronico" />
                  </div>
                  <div class="sed-field">
                    <label>Término de Duración</label>
                    <input type="text" class="sed-input" formControlName="terminoDuracion" />
                  </div>
                </div>
                <div class="sed-field" style="margin-bottom: 16px;">
                  <label>Objeto Social</label>
                  <textarea class="sed-input" formControlName="objetoSocial" rows="4"></textarea>
                </div>
                @if (errorEdicion()) {
                  <div class="sed-error-msg" style="margin-bottom: 12px;">⚠️ {{ errorEdicion() }}</div>
                }
                <div style="display: flex; gap: 8px;">
                  <button type="submit" class="sed-btn-primary" [disabled]="guardandoEdicion()">
                    @if (guardandoEdicion()) { Guardando... } @else { Guardar }
                  </button>
                  <button type="button" class="sed-btn-secondary" (click)="cancelarEdicion()">Cancelar</button>
                </div>
              </form>
            }
          </div>
        }

        <!-- Tab: Estado -->
        @if (tabActiva() === 'estado') {
          <div class="sed-card">
            <h4 style="font-size: 15px; font-weight: 600; margin-bottom: 16px;">Estado actual</h4>
            <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 24px;">
              <span [class]="'sed-chip ' + chipEstado(esal()!.estado)" style="font-size: 14px; padding: 6px 16px;">
                {{ labelEstado(esal()!.estado) }}
              </span>
            </div>
            <h4 style="font-size: 15px; font-weight: 600; margin-bottom: 12px;">Cambiar estado</h4>
            <div style="display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap;">
              <div class="sed-field" style="min-width: 200px;">
                <label>Nuevo estado</label>
                <select class="sed-input" [(ngModel)]="nuevoEstado">
                  <option value="">Seleccionar...</option>
                  <option value="ACTIVO">Activo</option>
                  <option value="SUSPENDIDO">Suspendido</option>
                  <option value="EN_LIQUIDACION">En Liquidación</option>
                  <option value="CANCELADO">Cancelado</option>
                </select>
              </div>
              <button
                class="sed-btn-primary"
                [disabled]="!nuevoEstado || cambiandoEstado()"
                (click)="cambiarEstado()"
              >
                @if (cambiandoEstado()) { Cambiando... } @else { Confirmar cambio }
              </button>
            </div>
            @if (errorEstado()) {
              <div class="sed-error-msg" style="margin-top: 12px;">⚠️ {{ errorEstado() }}</div>
            }
            @if (mensajeEstado()) {
              <div style="margin-top: 12px; color: #155724; font-size: 13px;">✅ {{ mensajeEstado() }}</div>
            }
          </div>
        }

        <!-- Tab: Completitud -->
        @if (tabActiva() === 'completitud') {
          <div class="sed-card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
              <h4 style="font-size: 15px; font-weight: 600;">Semáforo de Completitud</h4>
              <button class="sed-btn-secondary" (click)="recalcularCompletitud()" [disabled]="recalculando()">
                @if (recalculando()) { Recalculando... } @else { 🔄 Recalcular }
              </button>
            </div>

            @if (completitud()) {
              <div style="display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap;">
                <div class="stat-box">
                  <span class="stat-label">Estado</span>
                  <span [class]="'sed-chip ' + chipCompletitud(completitud()!.estadoCompletitud)">
                    {{ labelCompletitud(completitud()!.estadoCompletitud) }}
                  </span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">Total advertencias</span>
                  <span class="stat-valor">{{ completitud()!.totalAdvertencias }}</span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">Bloqueantes</span>
                  <span class="stat-valor" style="color: var(--color-error);">{{ completitud()!.advertenciasBloqueantes }}</span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">No bloqueantes</span>
                  <span class="stat-valor" style="color: #856404;">{{ completitud()!.advertenciasNoBloqueantes }}</span>
                </div>
              </div>

              @if ((completitud()!.advertencias?.length ?? 0) > 0) {
                <h5 style="font-size: 13px; font-weight: 600; margin-bottom: 8px;">Advertencias</h5>
                <table class="sed-table">
                  <thead>
                    <tr>
                      <th>Sección</th>
                      <th>Campo</th>
                      <th>Tipo</th>
                      <th>Bloqueante</th>
                      <th>Mensaje</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (adv of completitud()!.advertencias; track adv.campo) {
                      <tr>
                        <td>{{ adv.seccion }}</td>
                        <td>{{ adv.campo }}</td>
                        <td>{{ adv.tipo }}</td>
                        <td>
                          <span [class]="adv.bloqueante ? 'sed-chip sed-chip--bloqueante' : 'sed-chip sed-chip--incompleto'">
                            {{ adv.bloqueante ? 'Sí' : 'No' }}
                          </span>
                        </td>
                        <td>{{ adv.mensaje }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              } @else {
                <p style="color: #155724; font-size: 13px;">✅ Sin advertencias. La ESAL está lista para certificar.</p>
              }
            } @else {
              <p style="color: var(--color-on-surface-variant); font-size: 13px;">
                Haga clic en "Recalcular" para calcular la completitud.
              </p>
            }
          </div>
        }

        <!-- Tab: Documentos -->
        @if (tabActiva() === 'documentos') {
          <div class="sed-card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
              <h4 style="font-size: 15px; font-weight: 600;">Documentos Soporte</h4>
              <div>
                <input
                  type="file"
                  accept="application/pdf"
                  #fileInput
                  style="display: none;"
                  (change)="subirDocumento($event)"
                />
                <button class="sed-btn-primary" (click)="fileInput.click()" [disabled]="subiendoDoc()">
                  @if (subiendoDoc()) { Subiendo... } @else { 📎 Subir PDF }
                </button>
              </div>
            </div>

            @if (errorDocumento()) {
              <div class="sed-error-msg" style="margin-bottom: 12px;">⚠️ {{ errorDocumento() }}</div>
            }

            @if (documentos().length === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 24px;">
                No hay documentos soporte registrados.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Tamaño</th>
                    <th>Estado</th>
                    <th>Subido por</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  @for (doc of documentos(); track doc.id) {
                    <tr>
                      <td>{{ doc.nombreArchivo }}</td>
                      <td>{{ doc.tipoDocumento ?? '—' }}</td>
                      <td>{{ formatBytes(doc.tamanoBytes) }}</td>
                      <td>{{ doc.estadoValidacion }}</td>
                      <td>{{ doc.createdBy }}</td>
                      <td>{{ doc.createdAt | date:'dd/MM/yyyy' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .tabs-nav {
      display: flex;
      gap: 4px;
      border-bottom: 2px solid var(--color-outline-variant);
      padding-bottom: 0;
    }
    .tab-btn {
      padding: 8px 16px;
      background: none;
      border: none;
      border-bottom: 2px solid transparent;
      margin-bottom: -2px;
      font-size: 13px;
      font-weight: 600;
      color: var(--color-on-surface-variant);
      cursor: pointer;
      transition: color 0.2s, border-color 0.2s;
    }
    .tab-btn:hover { color: var(--color-primary-container); }
    .tab-btn--active {
      color: var(--color-primary-container);
      border-bottom-color: var(--color-primary-container);
    }
    .info-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
    }
    .info-item {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .info-item--full { grid-column: 1 / -1; }
    .info-label {
      font-size: 11px;
      font-weight: 600;
      color: var(--color-on-surface-variant);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .info-valor {
      font-size: 14px;
      color: var(--color-on-surface);
    }
    .stat-box {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      padding: 12px 16px;
      background: var(--color-surface-container-low);
      border-radius: var(--radius-lg);
      min-width: 100px;
    }
    .stat-label {
      font-size: 11px;
      font-weight: 600;
      color: var(--color-on-surface-variant);
      text-transform: uppercase;
    }
    .stat-valor {
      font-size: 22px;
      font-weight: 700;
      color: var(--color-primary);
    }
  `],
})
export class AdminEsalesDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly id = this.route.snapshot.paramMap.get('id') ?? '';

  esal = signal<EsalDetalle | null>(null);
  cargando = signal(false);
  error = signal<string | null>(null);

  tabActiva = signal<TabActiva>('info');

  // Edición
  editandoInfo = signal(false);
  guardandoEdicion = signal(false);
  errorEdicion = signal<string | null>(null);
  formularioEdicion: FormGroup = this.fb.group({
    nombre: [''],
    idSipej: [''],
    nit: [''],
    domicilio: [''],
    correoElectronico: [''],
    terminoDuracion: [''],
    objetoSocial: [''],
  });

  // Estado
  nuevoEstado = '';
  cambiandoEstado = signal(false);
  errorEstado = signal<string | null>(null);
  mensajeEstado = signal<string | null>(null);

  // Completitud
  completitud = signal<CompletitudResponse | null>(null);
  recalculando = signal(false);

  // Documentos
  documentos = signal<DocumentoSoporte[]>([]);
  subiendoDoc = signal(false);
  errorDocumento = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarEsal();
    this.cargarCompletitud();
    this.cargarDocumentos();
  }

  cargarEsal(): void {
    this.cargando.set(true);
    this.api.get<EsalDetalle>(`/api/esales/${this.id}`).subscribe({
      next: (esal) => {
        this.esal.set(esal);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la ESAL.');
        this.cargando.set(false);
      },
    });
  }

  volver(): void {
    this.router.navigate(['/admin/esales']);
  }

  irAMantenimiento(): void {
    this.router.navigate(['/admin/esales', this.id, 'mantenimiento']);
  }

  // --- Edición ---

  iniciarEdicion(): void {
    const e = this.esal();
    if (!e) return;
    this.formularioEdicion.patchValue({
      nombre: e.nombre,
      idSipej: e.idSipej ?? '',
      nit: e.nit ?? '',
      domicilio: e.domicilio ?? '',
      correoElectronico: e.correoElectronico ?? '',
      terminoDuracion: e.terminoDuracion ?? '',
      objetoSocial: e.objetoSocial ?? '',
    });
    this.editandoInfo.set(true);
  }

  cancelarEdicion(): void {
    this.editandoInfo.set(false);
    this.errorEdicion.set(null);
  }

  guardarEdicion(): void {
    this.guardandoEdicion.set(true);
    this.errorEdicion.set(null);
    this.api.put<EsalDetalle>(`/api/esales/${this.id}`, this.formularioEdicion.value).subscribe({
      next: (updated) => {
        this.esal.set(updated as EsalDetalle);
        this.editandoInfo.set(false);
        this.guardandoEdicion.set(false);
        this.cargarEsal();
      },
      error: (err) => {
        this.errorEdicion.set(err?.error?.message ?? 'Error al guardar.');
        this.guardandoEdicion.set(false);
      },
    });
  }

  // --- Estado ---

  cambiarEstado(): void {
    if (!this.nuevoEstado) return;
    this.cambiandoEstado.set(true);
    this.errorEstado.set(null);
    this.mensajeEstado.set(null);
    this.api.put<any>(`/api/esales/${this.id}/estado`, { estado: this.nuevoEstado }).subscribe({
      next: (updated) => {
        this.mensajeEstado.set(`Estado cambiado a ${this.labelEstado(this.nuevoEstado as EstadoEsal)}`);
        this.nuevoEstado = '';
        this.cambiandoEstado.set(false);
        this.cargarEsal();
      },
      error: (err) => {
        this.errorEstado.set(err?.error?.message ?? 'Error al cambiar el estado.');
        this.cambiandoEstado.set(false);
      },
    });
  }

  // --- Completitud ---

  cargarCompletitud(): void {
    this.api.get<CompletitudResponse>(`/api/esales/${this.id}/completitud`).subscribe({
      next: (c) => this.completitud.set(c),
      error: () => {},
    });
  }

  recalcularCompletitud(): void {
    this.recalculando.set(true);
    this.api.post<CompletitudResponse>(`/api/esales/${this.id}/completitud/recalcular`, {}).subscribe({
      next: (c) => {
        this.completitud.set(c);
        this.recalculando.set(false);
        this.cargarEsal();
      },
      error: () => this.recalculando.set(false),
    });
  }

  // --- Documentos ---

  cargarDocumentos(): void {
    this.api.get<DocumentoSoporte[]>(`/api/esales/${this.id}/documentos`).subscribe({
      next: (docs) => this.documentos.set(docs),
      error: () => {},
    });
  }

  subirDocumento(event: Event): void {
    const input = event.target as HTMLInputElement;
    const archivo = input.files?.[0];
    if (!archivo) return;

    if (archivo.type !== 'application/pdf') {
      this.errorDocumento.set('Solo se aceptan archivos PDF.');
      return;
    }

    const formData = new FormData();
    formData.append('archivo', archivo);

    this.subiendoDoc.set(true);
    this.errorDocumento.set(null);

    this.api.postForm<DocumentoSoporte>(`/api/esales/${this.id}/documentos`, formData).subscribe({
      next: (doc) => {
        this.documentos.update((docs) => [...docs, doc]);
        this.subiendoDoc.set(false);
        input.value = '';
      },
      error: (err) => {
        this.errorDocumento.set(err?.error?.message ?? 'Error al subir el documento.');
        this.subiendoDoc.set(false);
      },
    });
  }

  // --- Helpers ---

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

  formatBytes(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
