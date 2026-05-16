import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import {
  EsalDetalle,
  CompletitudResponse,
  DocumentoSoporte,
  EstadoEsal,
} from '../../core/models/esal.model';

type TabActiva = 'info' | 'completitud' | 'documentos';

@Component({
  selector: 'app-esales-detail',
  standalone: true,
  imports: [CommonModule],
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
        <!-- Header -->
        <div class="sed-card" style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
          <div>
            <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary);">{{ esal()!.nombre }}</h3>
            <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-top: 4px;">
              ID SIPEJ: {{ esal()!.idSipej ?? '—' }} | NIT: {{ esal()!.nit ?? '—' }}
            </p>
          </div>
          <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">
            <span [class]="'sed-chip ' + chipEstado(esal()!.estado)">{{ labelEstado(esal()!.estado) }}</span>
            <span [class]="'sed-chip ' + chipCompletitud(esal()!.estadoCompletitud)">{{ labelCompletitud(esal()!.estadoCompletitud) }}</span>
            <button class="sed-btn-primary" style="padding: 6px 14px; font-size: 12px;" (click)="verPreview()">
              📋 Vista Previa
            </button>
            <button class="sed-btn-secondary" style="padding: 6px 14px; font-size: 12px;" (click)="verHistorial()">
              📜 Historial
            </button>
          </div>
        </div>

        <!-- Tabs -->
        <div class="tabs-nav" style="margin-bottom: 16px;">
          <button [class]="'tab-btn' + (tabActiva() === 'info' ? ' tab-btn--active' : '')" (click)="tabActiva.set('info')">
            📋 Información
          </button>
          <button [class]="'tab-btn' + (tabActiva() === 'completitud' ? ' tab-btn--active' : '')" (click)="cargarCompletitud(); tabActiva.set('completitud')">
            🚦 Completitud
          </button>
          <button [class]="'tab-btn' + (tabActiva() === 'documentos' ? ' tab-btn--active' : '')" (click)="cargarDocumentos(); tabActiva.set('documentos')">
            📄 Documentos
          </button>
        </div>

        <!-- Tab: Información -->
        @if (tabActiva() === 'info') {
          <div class="sed-card">
            <div class="info-grid">
              <div class="info-item"><span class="info-label">Nombre</span><span class="info-valor">{{ esal()!.nombre }}</span></div>
              <div class="info-item"><span class="info-label">ID SIPEJ</span><span class="info-valor">{{ esal()!.idSipej ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">NIT</span><span class="info-valor">{{ esal()!.nit ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Domicilio</span><span class="info-valor">{{ esal()!.domicilio ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Correo Electrónico</span><span class="info-valor">{{ esal()!.correoElectronico ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Término de Duración</span><span class="info-valor">{{ esal()!.terminoDuracion ?? '—' }}</span></div>
              <div class="info-item info-item--full"><span class="info-label">Objeto Social</span><span class="info-valor">{{ esal()!.objetoSocial ?? '—' }}</span></div>
            </div>
          </div>
        }

        <!-- Tab: Completitud -->
        @if (tabActiva() === 'completitud') {
          <div class="sed-card">
            <h4 style="font-size: 15px; font-weight: 600; margin-bottom: 16px;">Semáforo de Completitud</h4>

            @if (completitud()) {
              <div style="display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap;">
                <div class="stat-box">
                  <span class="stat-label">Estado</span>
                  <span [class]="'sed-chip ' + chipCompletitud(completitud()!.estadoCompletitud)">
                    {{ labelCompletitud(completitud()!.estadoCompletitud) }}
                  </span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">Total</span>
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
                <table class="sed-table">
                  <thead>
                    <tr>
                      <th>Sección</th>
                      <th>Campo</th>
                      <th>Bloqueante</th>
                      <th>Mensaje</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (adv of completitud()!.advertencias; track adv.campo) {
                      <tr>
                        <td>{{ adv.seccion }}</td>
                        <td>{{ adv.campo }}</td>
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
                Cargando completitud...
              </p>
            }
          </div>
        }

        <!-- Tab: Documentos -->
        @if (tabActiva() === 'documentos') {
          <div class="sed-card">
            <h4 style="font-size: 15px; font-weight: 600; margin-bottom: 16px;">Documentos Soporte</h4>
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
    .info-item { display: flex; flex-direction: column; gap: 4px; }
    .info-item--full { grid-column: 1 / -1; }
    .info-label {
      font-size: 11px;
      font-weight: 600;
      color: var(--color-on-surface-variant);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .info-valor { font-size: 14px; color: var(--color-on-surface); }
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
export class EsalesDetailComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly id = this.route.snapshot.paramMap.get('id') ?? '';

  esal = signal<EsalDetalle | null>(null);
  cargando = signal(false);
  error = signal<string | null>(null);
  tabActiva = signal<TabActiva>('info');
  completitud = signal<CompletitudResponse | null>(null);
  documentos = signal<DocumentoSoporte[]>([]);

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

  cargarCompletitud(): void {
    this.api.get<CompletitudResponse>(`/api/esales/${this.id}/completitud`).subscribe({
      next: (c) => this.completitud.set(c),
      error: () => {},
    });
  }

  cargarDocumentos(): void {
    this.api.get<DocumentoSoporte[]>(`/api/esales/${this.id}/documentos`).subscribe({
      next: (docs) => this.documentos.set(docs),
      error: () => {},
    });
  }

  volver(): void {
    this.router.navigate(['/esales']);
  }

  verPreview(): void {
    this.router.navigate(['/certificados', 'preview', this.id]);
  }

  verHistorial(): void {
    this.router.navigate(['/esales', this.id, 'certificados']);
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

  formatBytes(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
