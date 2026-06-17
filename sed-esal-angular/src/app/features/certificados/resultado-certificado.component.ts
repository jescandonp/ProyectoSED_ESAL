import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { CertificadoDto, EstadoCertificado } from '../../core/models/esal.model';

@Component({
  selector: 'app-resultado-certificado',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <header class="sed-page-header">
        <div>
          <span class="sed-page-kicker">Certificados</span>
          <h2 class="sed-page-title">Resultado de Certificado</h2>
          <p class="sed-page-subtitle">Número, trazabilidad y descarga autenticada del certificado generado.</p>
        </div>
        <button class="sed-btn-secondary" (click)="volver()">
          <i class="pi pi-arrow-left" aria-hidden="true"></i>
          Volver
        </button>
      </header>

      @if (cargando()) {
        <div class="sed-loading-state" role="status">
          <i class="pi pi-spin pi-spinner" aria-hidden="true"></i>
          <span>Cargando certificado...</span>
        </div>
      } @else if (error()) {
        <div class="sed-alert sed-alert--error" role="alert">
          <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
          <span>{{ error() }}</span>
        </div>
      } @else if (cert()) {
        <!-- Estado -->
        <div class="sed-section" style="margin-bottom: 16px;">
          <div class="sed-toolbar">
          <div>
            <h3 style="font-size: 18px; font-weight: 700; color: var(--color-primary);">
              {{ cert()!.numeroCertificado ?? '—' }}
            </h3>
            <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-top: 4px;">
              Expedido: {{ cert()!.fechaExpedicion | date:'dd/MM/yyyy HH:mm' }}
            </p>
          </div>
          <span [class]="'sed-chip ' + chipEstado(cert()!.estadoCertificado)">
            {{ labelEstado(cert()!.estadoCertificado) }}
          </span>
          </div>
        </div>

        @if (cert()!.errorDetalle) {
          <div class="sed-alert sed-alert--error" style="margin-bottom: 16px;" role="alert">
            <i class="pi pi-exclamation-triangle" aria-hidden="true"></i>
            <span>{{ cert()!.errorDetalle }}</span>
          </div>
        }

        <!-- Detalle -->
        <div class="sed-section" style="margin-bottom: 16px;">
          <h3 class="sed-section-title">
            <i class="pi pi-shield" aria-hidden="true"></i>
            Trazabilidad
          </h3>
          <div class="info-grid">
            <div class="info-item"><span class="info-label">ID SIPEJ</span><span class="info-valor">{{ cert()!.idSipej ?? '—' }}</span></div>
            <div class="info-item"><span class="info-label">NIT</span><span class="info-valor">{{ cert()!.nit ?? '—' }}</span></div>
            <div class="info-item"><span class="info-label">Firmante</span><span class="info-valor">{{ cert()!.firmanteNombre ?? '—' }}</span></div>
            <div class="info-item"><span class="info-label">Cargo</span><span class="info-valor">{{ cert()!.firmanteCargo ?? '—' }}</span></div>
            <div class="info-item"><span class="info-label">Plantilla</span><span class="info-valor">{{ cert()!.plantillaVersion ?? '—' }}</span></div>
            <div class="info-item"><span class="info-label">Tamaño</span><span class="info-valor">{{ formatBytes(cert()!.tamanoBytes) }}</span></div>
            <div class="info-item info-item--full">
              <span class="info-label">Hash SHA-256</span>
              <span class="info-valor" style="font-family: monospace; font-size: 12px; word-break: break-all;">{{ cert()!.hashSha256 ?? '—' }}</span>
            </div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="sed-actions">
          <button class="sed-btn-secondary" (click)="volver()">
            <i class="pi pi-arrow-left" aria-hidden="true"></i>
            Volver a ESAL
          </button>
          @if (cert()!.estadoCertificado === 'GENERADO') {
            <button class="btn-descargar" type="button" [disabled]="descargando()" (click)="descargarPdf()">
              @if (descargando()) {
                <i class="pi pi-spin pi-spinner" aria-hidden="true"></i>
                Descargando...
              } @else {
                <i class="pi pi-download" aria-hidden="true"></i>
                Descargar PDF
              }
            </button>
          }
          <button class="sed-btn-secondary" (click)="verHistorial()">
            <i class="pi pi-list" aria-hidden="true"></i>
            Ver historial
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .info-item { display: flex; flex-direction: column; gap: 4px; }
    .info-item--full { grid-column: 1 / -1; }
    .info-label { font-size: 11px; font-weight: 600; color: var(--color-on-surface-variant); text-transform: uppercase; letter-spacing: 0.05em; }
    .info-valor { font-size: 14px; color: var(--color-on-surface); }
    .btn-descargar {
      display: inline-flex; align-items: center; justify-content: center; gap: var(--space-xs);
      background: #1b5e20; color: #fff; padding: 8px 18px; border-radius: 6px;
      border: none; cursor: pointer; text-decoration: none; font-size: 0.9rem; font-weight: 600;
    }
  `],
})
export class ResultadoCertificadoComponent implements OnInit {
  private readonly api    = inject(ApiService);
  private readonly route  = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly certificadoId = this.route.snapshot.paramMap.get('certificadoId') ?? '';
  private esalId: number | null = null;

  cert     = signal<CertificadoDto | null>(null);
  cargando = signal(false);
  error    = signal<string | null>(null);
  descargando = signal(false);

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.get<CertificadoDto>(`/api/certificados/${this.certificadoId}`).subscribe({
      next: (c) => { this.cert.set(c); this.esalId = c.esalId; this.cargando.set(false); },
      error: () => { this.error.set('No se pudo cargar el certificado.'); this.cargando.set(false); },
    });
  }

  volver(): void {
    if (this.esalId) this.router.navigate(['/esales', this.esalId]);
    else this.router.navigate(['/esales']);
  }

  verHistorial(): void {
    if (this.esalId) this.router.navigate(['/esales', this.esalId, 'certificados']);
    else this.router.navigate(['/esales']);
  }

  descargarPdf(): void {
    this.descargando.set(true);
    this.api.download(`/api/certificados/${this.certificadoId}/descargar`).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const enlace = document.createElement('a');
        enlace.href = url;
        enlace.download = this.cert()?.nombreArchivo ?? 'certificado.pdf';
        enlace.click();
        URL.revokeObjectURL(url);
        this.descargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo descargar el certificado.');
        this.descargando.set(false);
      },
    });
  }

  chipEstado(e: EstadoCertificado): string {
    if (e === 'GENERADO') return 'sed-chip--listo';
    if (e === 'BLOQUEADO') return 'sed-chip--bloqueante';
    return 'sed-chip--suspendido';
  }

  labelEstado(e: EstadoCertificado): string {
    if (e === 'GENERADO') return 'Generado';
    if (e === 'BLOQUEADO') return 'Bloqueado';
    return 'Fallido';
  }

  formatBytes(bytes: number | null): string {
    if (!bytes) return '—';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
