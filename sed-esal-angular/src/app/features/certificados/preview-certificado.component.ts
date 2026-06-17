import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { PreviewCertificado, EstadoEsal, EstadoCompletitud, CertificadoDto } from '../../core/models/esal.model';

@Component({
  selector: 'app-preview-certificado',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <header class="sed-page-header">
        <div>
          <span class="sed-page-kicker">Certificados</span>
          <h2 class="sed-page-title">Vista Previa del Certificado</h2>
          <p class="sed-page-subtitle">Revise bloqueos, advertencias y contenido antes de emitir el certificado.</p>
        </div>
        <button class="sed-btn-secondary" (click)="volver()">
          <i class="pi pi-arrow-left" aria-hidden="true"></i>
          Volver
        </button>
      </header>

      @if (cargando()) {
        <div class="sed-loading-state" role="status">
          <i class="pi pi-spin pi-spinner" aria-hidden="true"></i>
          <span>Generando vista previa...</span>
        </div>
      } @else if (error()) {
        <div class="sed-alert sed-alert--error" role="alert">
          <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
          <span>{{ error() }}</span>
        </div>
      } @else if (preview()) {
        <!-- Encabezado -->
        <div class="sed-section" style="margin-bottom: 16px;">
          <div class="sed-toolbar">
          <div>
            <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary);">{{ preview()!.nombre }}</h3>
            <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-top: 4px;">
              ID SIPEJ: {{ preview()!.idSipej ?? '—' }}&nbsp;&nbsp;|&nbsp;&nbsp;NIT: {{ preview()!.nit ?? '—' }}
            </p>
            @if (preview()!.versionDatos) {
              <p style="font-size: 11px; color: var(--color-on-surface-variant); margin-top: 2px;">
                Versión de datos: {{ preview()!.versionDatos | date:'dd/MM/yyyy HH:mm' }}
              </p>
            }
          </div>
          <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 8px;">
            <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap;">
              <span [class]="'sed-chip ' + chipEstado(preview()!.estado)">{{ labelEstado(preview()!.estado) }}</span>
              <span [class]="'sed-chip ' + chipCompletitud(preview()!.estadoCompletitud)">{{ labelCompletitud(preview()!.estadoCompletitud) }}</span>
            </div>
            <div class="habilitada-badge"
                 [class.habilitada-badge--si]="preview()!.generacionHabilitada"
                 [class.habilitada-badge--no]="!preview()!.generacionHabilitada">
              @if (preview()!.generacionHabilitada) {
                <i class="pi pi-check-circle" aria-hidden="true"></i>
                Generación habilitada
              } @else {
                <i class="pi pi-ban" aria-hidden="true"></i>
                Generación no habilitada
              }
            </div>
          </div>
          </div>
        </div>

        <!-- Alerta de estado -->
        @if (preview()!.alertaEstado) {
          <div class="sed-alert sed-alert--warning" style="margin-bottom: 16px;">
            <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
            <span>{{ preview()!.alertaEstado }}</span>
          </div>
        }

        <!-- Advertencias -->
        @if ((preview()!.advertencias?.length ?? 0) > 0) {
          <div class="sed-card" style="margin-bottom: 16px; background: #fff8e1; border-left: 4px solid #f59e0b;">
            <h4 style="font-size: 14px; font-weight: 600; margin-bottom: 8px; color: #92400e;">Advertencias</h4>
            <ul style="margin: 0; padding-left: 18px; font-size: 13px; color: #78350f;">
              @for (adv of preview()!.advertencias; track adv) {
                <li style="margin-bottom: 4px;">{{ adv }}</li>
              }
            </ul>
          </div>
        }

        <!-- Bloqueos -->
        @if ((preview()!.bloqueos?.length ?? 0) > 0) {
          <div class="sed-card" style="margin-bottom: 16px; background: #fef2f2; border-left: 4px solid var(--color-error);">
            <h4 style="font-size: 14px; font-weight: 600; margin-bottom: 12px; color: var(--color-error);">
              Bloqueos ({{ preview()!.bloqueos.length }})
            </h4>
            <table class="sed-table">
              <thead>
                <tr><th>Sección</th><th>Campo</th><th>Mensaje</th></tr>
              </thead>
              <tbody>
                @for (b of preview()!.bloqueos; track b.campo) {
                  <tr>
                    <td>{{ b.seccion }}</td>
                    <td style="font-weight: 600;">{{ b.campo }}</td>
                    <td>{{ b.mensaje }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }

        <!-- Secciones -->
        <h3 style="font-size: 15px; font-weight: 600; margin-bottom: 12px; color: var(--color-primary);">
          Contenido del Certificado
        </h3>
        @for (seccion of preview()!.secciones; track seccion.nombre) {
          <div class="sed-card" style="margin-bottom: 12px;">
            <h4 class="seccion-titulo">{{ seccion.nombre }}</h4>
            <div class="info-grid">
              @for (campo of seccion.campos; track campo.etiqueta) {
                <div class="info-item" [class.info-item--faltante]="campo.faltante && campo.obligatorio">
                  <span class="info-label">
                    {{ campo.etiqueta }}
                    @if (campo.obligatorio) { <span class="obligatorio-mark">*</span> }
                  </span>
                  @if (campo.faltante) {
                    <span class="valor-faltante">
                      @if (campo.obligatorio) { ⛔ Faltante (obligatorio) }
                      @else { — Sin datos }
                    </span>
                  } @else {
                    <span class="info-valor">{{ campo.valor }}</span>
                  }
                </div>
              }
            </div>
          </div>
        }

        <!-- Pie -->
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; margin-top: 24px;">
          <button class="sed-btn-secondary" (click)="volver()">Volver al detalle</button>
          @if (!preview()!.generacionHabilitada) {
            <button class="btn-generar" disabled title="Resuelva los bloqueos para habilitar la generación">
              <i class="pi pi-lock" aria-hidden="true"></i>
              Generación bloqueada
            </button>
          } @else if (generando()) {
            <button class="btn-generar" disabled>
              <i class="pi pi-spin pi-spinner" aria-hidden="true"></i>
              Generando...
            </button>
          } @else {
            <button class="btn-generar" (click)="confirmarYGenerar()">
              <i class="pi pi-file-pdf" aria-hidden="true"></i>
              Generar certificado
            </button>
          }
        </div>

        @if (errorGeneracion()) {
          <div class="sed-alert sed-alert--error" style="margin-top: 12px;" role="alert">
            <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
            <span>{{ errorGeneracion() }}</span>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .btn-generar {
      display: inline-flex; align-items: center; justify-content: center; gap: var(--space-xs);
      background: #1b5e20; color: #fff; border: none; padding: 10px 22px;
      border-radius: 6px; cursor: pointer; font-size: 0.92rem; font-weight: 600;
    }
    .btn-generar:disabled { cursor: not-allowed; opacity: 0.6; }
    .habilitada-badge { display: inline-flex; align-items: center; gap: var(--space-xs); padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 600; }
    .habilitada-badge--si { background: #d1fae5; color: #065f46; border: 1px solid #6ee7b7; }
    .habilitada-badge--no { background: #fef2f2; color: var(--color-error); border: 1px solid #fca5a5; }
    .alerta-estado {
      background: #fef3c7; border: 1px solid #f59e0b; border-left: 4px solid #d97706;
      border-radius: 6px; padding: 12px 16px; font-size: 14px; font-weight: 600; color: #78350f;
    }
    .seccion-titulo {
      font-size: 13px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em;
      color: var(--color-primary-container); margin-bottom: 16px; padding-bottom: 8px;
      border-bottom: 1px solid var(--color-outline-variant);
    }
    .info-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 12px; }
    .info-item { display: flex; flex-direction: column; gap: 4px; padding: 8px; border-radius: 4px; }
    .info-item--faltante { background: #fff5f5; }
    .info-label { font-size: 11px; font-weight: 600; color: var(--color-on-surface-variant); text-transform: uppercase; letter-spacing: 0.05em; }
    .info-valor { font-size: 14px; color: var(--color-on-surface); }
    .obligatorio-mark { color: var(--color-error); margin-left: 2px; }
    .valor-faltante { font-size: 13px; color: var(--color-error); font-style: italic; }
  `],
})
export class PreviewCertificadoComponent implements OnInit {
  private readonly api    = inject(ApiService);
  private readonly route  = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly id = this.route.snapshot.paramMap.get('id') ?? '';

  preview        = signal<PreviewCertificado | null>(null);
  cargando       = signal(false);
  error          = signal<string | null>(null);
  generando      = signal(false);
  errorGeneracion = signal<string | null>(null);

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.get<PreviewCertificado>(`/api/certificados/preview/esales/${this.id}`).subscribe({
      next: (p) => { this.preview.set(p); this.cargando.set(false); },
      error: () => { this.error.set('No se pudo cargar la vista previa del certificado.'); this.cargando.set(false); },
    });
  }

  volver(): void { this.router.navigate(['/esales', this.id]); }

  confirmarYGenerar(): void {
    if (!confirm(`¿Confirma la generación del certificado para "${this.preview()!.nombre}"?\n\nSe asignará un número único que no podrá reutilizarse.`)) return;
    this.generando.set(true);
    this.errorGeneracion.set(null);
    this.api.post<CertificadoDto>(`/api/certificados/esales/${this.id}/generar`, {}).subscribe({
      next: (cert) => {
        this.generando.set(false);
        this.router.navigate(['/certificados', cert.certificadoId]);
      },
      error: (err) => {
        this.generando.set(false);
        this.errorGeneracion.set(err?.error?.message ?? 'Error al generar el certificado.');
      },
    });
  }

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
}
