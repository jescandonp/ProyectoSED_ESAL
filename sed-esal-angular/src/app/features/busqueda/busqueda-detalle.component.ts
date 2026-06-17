import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import {
  BusquedaDetalle,
  EstadoEsal,
  EstadoCompletitud,
} from '../../core/models/esal.model';

type Tab = 'info' | 'personeria' | 'reformas' | 'nombramientos' | 'organos' | 'actuaciones' | 'documentos' | 'completitud';

@Component({
  selector: 'app-busqueda-detalle',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <header class="sed-page-header">
        <div>
          <span class="sed-page-kicker">Consulta ESAL</span>
          <h2 class="sed-page-title">Detalle de Entidad</h2>
          <p class="sed-page-subtitle">Información administrativa, soporte documental y estado de completitud.</p>
        </div>
        <button class="sed-btn-secondary" (click)="volver()">
          <i class="pi pi-arrow-left" aria-hidden="true"></i>
          Volver
        </button>
      </header>

      @if (cargando()) {
        <div class="sed-loading-state" role="status">
          <i class="pi pi-spin pi-spinner" aria-hidden="true"></i>
          <span>Cargando detalle de ESAL...</span>
        </div>
      } @else if (error()) {
        <div class="sed-alert sed-alert--error" role="alert">
          <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
          <span>{{ error() }}</span>
        </div>
      } @else if (detalle()) {
        <!-- Header -->
        <div class="sed-section" style="margin-bottom: 16px;">
          <div class="sed-toolbar">
          <div>
            <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary);">{{ detalle()!.nombre }}</h3>
            <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-top: 4px;">
              ID SIPEJ: {{ detalle()!.idSipej ?? '—' }}&nbsp;&nbsp;|&nbsp;&nbsp;NIT: {{ detalle()!.nit ?? '—' }}
            </p>
          </div>
          <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">
            <span [class]="'sed-chip ' + chipEstado(detalle()!.estado)">{{ labelEstado(detalle()!.estado) }}</span>
            <span [class]="'sed-chip ' + chipCompletitud(detalle()!.estadoCompletitud)">{{ labelCompletitud(detalle()!.estadoCompletitud) }}</span>
            <button class="sed-btn-primary" style="padding: 6px 14px; font-size: 13px;" (click)="verPreview()">
              <i class="pi pi-file-pdf" aria-hidden="true"></i>
              Ver Preview Certificado
            </button>
          </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="tabs-nav" style="margin-bottom: 16px;">
          @for (tab of tabs; track tab.id) {
            <button
              [class]="'tab-btn' + (tabActiva() === tab.id ? ' tab-btn--active' : '')"
              (click)="tabActiva.set(tab.id)"
            >{{ tab.label }}</button>
          }
        </div>

        <!-- Tab: Información General -->
        @if (tabActiva() === 'info') {
          <div class="sed-card">
            <div class="info-grid">
              <div class="info-item"><span class="info-label">Nombre</span><span class="info-valor">{{ detalle()!.nombre }}</span></div>
              <div class="info-item"><span class="info-label">ID SIPEJ</span><span class="info-valor">{{ detalle()!.idSipej ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">NIT</span><span class="info-valor">{{ detalle()!.nit ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Domicilio</span><span class="info-valor">{{ detalle()!.domicilio ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Correo Electrónico</span><span class="info-valor">{{ detalle()!.correoElectronico ?? '—' }}</span></div>
              <div class="info-item"><span class="info-label">Término de Duración</span><span class="info-valor">{{ detalle()!.terminoDuracion ?? '—' }}</span></div>
              <div class="info-item info-item--full"><span class="info-label">Objeto Social</span><span class="info-valor">{{ detalle()!.objetoSocial ?? '—' }}</span></div>
            </div>
          </div>
        }

        <!-- Tab: Personería -->
        @if (tabActiva() === 'personeria') {
          <div class="sed-card">
            @if (detalle()!.personeria) {
              <div class="info-grid">
                <div class="info-item info-item--full"><span class="info-label">Reconocimiento PJ</span><span class="info-valor">{{ detalle()!.personeria!.reconocimiento ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Fecha Reconocimiento</span><span class="info-valor">{{ detalle()!.personeria!.fechaReconocimiento ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Entidad que Expide</span><span class="info-valor">{{ detalle()!.personeria!.entidadQueExpide ?? '—' }}</span></div>
                <div class="info-item info-item--full"><span class="info-label">Inscripción</span><span class="info-valor">{{ detalle()!.personeria!.inscripcion ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Fecha Inscripción</span><span class="info-valor">{{ detalle()!.personeria!.fechaInscripcion ?? '—' }}</span></div>
                <div class="info-item"><span class="info-label">Entidad que Inscribió</span><span class="info-valor">{{ detalle()!.personeria!.entidadQueInscribio ?? '—' }}</span></div>
              </div>
            } @else {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 24px;">
                Sin datos de personería jurídica registrados.
              </p>
            }
          </div>
        }

        <!-- Tab: Reformas -->
        @if (tabActiva() === 'reformas') {
          <div class="sed-card" style="padding: 0; overflow: hidden;">
            @if ((detalle()!.reformas?.length ?? 0) === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 32px;">
                Sin reformas estatutarias registradas.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr>
                    <th>#</th><th>Tipo Acto</th><th>Número</th><th>Fecha</th><th>Entidad</th><th>Descripción</th>
                  </tr>
                </thead>
                <tbody>
                  @for (r of detalle()!.reformas; track r.orden) {
                    <tr>
                      <td>{{ r.orden ?? '—' }}</td>
                      <td>{{ r.tipoActo ?? '—' }}</td>
                      <td>{{ r.numeroActo ?? '—' }}</td>
                      <td>{{ r.fechaActo ?? '—' }}</td>
                      <td>{{ r.entidadQueExpide ?? '—' }}</td>
                      <td>{{ r.descripcion ?? '—' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }

        <!-- Tab: Nombramientos -->
        @if (tabActiva() === 'nombramientos') {
          <div class="sed-card" style="padding: 0; overflow: hidden;">
            @if ((detalle()!.nombramientos?.length ?? 0) === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 32px;">
                Sin nombramientos registrados.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr>
                    <th>Tipo</th><th>Nombre</th><th>Documento</th><th>Cargo</th><th>Acta</th><th>Fecha Acta</th>
                  </tr>
                </thead>
                <tbody>
                  @for (n of detalle()!.nombramientos; track n.nombre) {
                    <tr>
                      <td>{{ n.tipoNombramiento ?? '—' }}</td>
                      <td>{{ n.nombre ?? '—' }}</td>
                      <td>{{ (n.tipoDocumento ?? '') + ' ' + (n.numeroDocumento ?? '') || '—' }}</td>
                      <td>{{ n.cargo ?? '—' }}</td>
                      <td>{{ n.actaAprueba ?? '—' }}</td>
                      <td>{{ n.fechaActa ?? '—' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }

        <!-- Tab: Órganos -->
        @if (tabActiva() === 'organos') {
          <div class="sed-card" style="padding: 0; overflow: hidden;">
            @if ((detalle()!.organos?.length ?? 0) === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 32px;">
                Sin órganos de administración registrados.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr><th>Órgano</th><th>Miembro</th><th>Cargo</th><th>Acta</th><th>Fecha Acta</th></tr>
                </thead>
                <tbody>
                  @for (o of detalle()!.organos; track o.miembro) {
                    <tr>
                      <td>{{ o.organo ?? '—' }}</td>
                      <td>{{ o.miembro ?? '—' }}</td>
                      <td>{{ o.cargo ?? '—' }}</td>
                      <td>{{ o.actaAprueba ?? '—' }}</td>
                      <td>{{ o.fechaActa ?? '—' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }

        <!-- Tab: Actuaciones -->
        @if (tabActiva() === 'actuaciones') {
          <div class="sed-card" style="padding: 0; overflow: hidden;">
            @if ((detalle()!.actuaciones?.length ?? 0) === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 32px;">
                Sin actuaciones administrativas registradas.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr><th>Tipo</th><th>Acta</th><th>Resolución</th><th>Fecha</th><th>Motivo</th></tr>
                </thead>
                <tbody>
                  @for (a of detalle()!.actuaciones; track a.tipoActuacion) {
                    <tr>
                      <td>{{ a.tipoActuacion ?? '—' }}</td>
                      <td>{{ a.acta ?? '—' }}</td>
                      <td>{{ a.resolucion ?? '—' }}</td>
                      <td>{{ a.fechaActa ?? a.fechaResolucion ?? '—' }}</td>
                      <td>{{ a.motivo ?? '—' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }

        <!-- Tab: Documentos -->
        @if (tabActiva() === 'documentos') {
          <div class="sed-card" style="padding: 0; overflow: hidden;">
            @if ((detalle()!.documentos?.length ?? 0) === 0) {
              <p style="color: var(--color-on-surface-variant); font-size: 13px; text-align: center; padding: 32px;">
                Sin documentos soporte registrados.
              </p>
            } @else {
              <table class="sed-table">
                <thead>
                  <tr><th>Nombre</th><th>Tipo</th><th>Tamaño</th><th>Estado</th><th>Fecha</th></tr>
                </thead>
                <tbody>
                  @for (d of detalle()!.documentos; track d.id) {
                    <tr>
                      <td>{{ d.nombreArchivo }}</td>
                      <td>{{ d.tipoDocumento ?? '—' }}</td>
                      <td>{{ formatBytes(d.tamanoBytes) }}</td>
                      <td>{{ d.estadoValidacion }}</td>
                      <td>{{ d.createdAt | date:'dd/MM/yyyy' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        }

        <!-- Tab: Completitud -->
        @if (tabActiva() === 'completitud') {
          <div class="sed-card">
            @if (detalle()!.completitud) {
              <div style="display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap;">
                <div class="stat-box">
                  <span class="stat-label">Estado</span>
                  <span [class]="'sed-chip ' + chipCompletitud(detalle()!.completitud!.estadoCompletitud)">
                    {{ labelCompletitud(detalle()!.completitud!.estadoCompletitud) }}
                  </span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">Total</span>
                  <span class="stat-valor">{{ detalle()!.completitud!.totalAdvertencias }}</span>
                </div>
                <div class="stat-box">
                  <span class="stat-label">Bloqueantes</span>
                  <span class="stat-valor" style="color: var(--color-error);">{{ detalle()!.completitud!.advertenciasBloqueantes }}</span>
                </div>
              </div>
              @if ((detalle()!.completitud!.advertencias?.length ?? 0) > 0) {
                <table class="sed-table">
                  <thead>
                    <tr><th>Sección</th><th>Campo</th><th>Bloqueante</th><th>Mensaje</th></tr>
                  </thead>
                  <tbody>
                    @for (adv of detalle()!.completitud!.advertencias; track adv.campo) {
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
            }
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .tabs-nav { display: flex; gap: 4px; border-bottom: 2px solid var(--color-outline-variant); flex-wrap: wrap; }
    .tab-btn {
      padding: 8px 14px; background: none; border: none;
      border-bottom: 2px solid transparent; margin-bottom: -2px;
      font-size: 12px; font-weight: 600; color: var(--color-on-surface-variant); cursor: pointer;
    }
    .tab-btn:hover { color: var(--color-primary-container); }
    .tab-btn--active { color: var(--color-primary-container); border-bottom-color: var(--color-primary-container); }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .info-item { display: flex; flex-direction: column; gap: 4px; }
    .info-item--full { grid-column: 1 / -1; }
    .info-label { font-size: 11px; font-weight: 600; color: var(--color-on-surface-variant); text-transform: uppercase; letter-spacing: 0.05em; }
    .info-valor { font-size: 14px; color: var(--color-on-surface); }
    .stat-box { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 12px 16px; background: var(--color-surface-container-low); border-radius: 8px; min-width: 100px; }
    .stat-label { font-size: 11px; font-weight: 600; color: var(--color-on-surface-variant); text-transform: uppercase; }
    .stat-valor { font-size: 22px; font-weight: 700; color: var(--color-primary); }
  `],
})
export class BusquedaDetalleComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly id = this.route.snapshot.paramMap.get('id') ?? '';

  detalle = signal<BusquedaDetalle | null>(null);
  cargando = signal(false);
  error = signal<string | null>(null);
  tabActiva = signal<Tab>('info');

  readonly tabs: { id: Tab; label: string }[] = [
    { id: 'info', label: 'Información' },
    { id: 'personeria', label: 'Personería' },
    { id: 'reformas', label: 'Reformas' },
    { id: 'nombramientos', label: 'Nombramientos' },
    { id: 'organos', label: 'Órganos' },
    { id: 'actuaciones', label: 'Actuaciones' },
    { id: 'documentos', label: 'Documentos' },
    { id: 'completitud', label: 'Completitud' },
  ];

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.get<BusquedaDetalle>(`/api/busquedas/esales/${this.id}`).subscribe({
      next: (d) => { this.detalle.set(d); this.cargando.set(false); },
      error: () => { this.error.set('No se pudo cargar el detalle de la ESAL.'); this.cargando.set(false); },
    });
  }

  volver(): void { this.router.navigate(['/busqueda']); }

  verPreview(): void { this.router.navigate(['/certificados/preview', this.id]); }

  chipEstado(estado: EstadoEsal): string {
    const map: Record<EstadoEsal, string> = { ACTIVO: 'sed-chip--activo', SUSPENDIDO: 'sed-chip--suspendido', EN_LIQUIDACION: 'sed-chip--liquidacion', CANCELADO: 'sed-chip--cancelado' };
    return map[estado] ?? '';
  }
  labelEstado(estado: EstadoEsal): string {
    const map: Record<EstadoEsal, string> = { ACTIVO: 'Activo', SUSPENDIDO: 'Suspendido', EN_LIQUIDACION: 'En Liquidación', CANCELADO: 'Cancelado' };
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
  formatBytes(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
