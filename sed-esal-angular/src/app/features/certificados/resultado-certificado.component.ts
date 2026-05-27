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
      <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
        <button class="sed-btn-secondary" style="padding: 6px 12px;" (click)="volver()">← Volver</button>
        <h2 class="sed-page-title" style="margin-bottom: 0;">Resultado de Certificado</h2>
      </div>

      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">Cargando...</div>
      } @else if (error()) {
        <div class="sed-card" style="color: var(--color-error); text-align: center; padding: 24px;">{{ error() }}</div>
      } @else if (cert()) {
        <!-- Estado -->
        <div class="sed-card" style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 12px;">
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

        @if (cert()!.errorDetalle) {
          <div class="sed-card" style="margin-bottom: 16px; background: #fef2f2; border-left: 4px solid var(--color-error);">
            <h4 style="font-size: 14px; font-weight: 600; color: var(--color-error); margin-bottom: 8px;">Error</h4>
            <p style="font-size: 13px;">{{ cert()!.errorDetalle }}</p>
          </div>
        }

        <!-- Detalle -->
        <div class="sed-card" style="margin-bottom: 16px;">
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
        <div style="display: flex; gap: 12px; flex-wrap: wrap;">
          <button class="sed-btn-secondary" (click)="volver()">← Volver a ESAL</button>
          @if (cert()!.estadoCertificado === 'GENERADO') {
            <button class="btn-descargar" type="button" [disabled]="descargando()" (click)="descargarPdf()">
              {{ descargando() ? 'Descargando...' : '⬇ Descargar PDF' }}
            </button>
          }
          <button class="sed-btn-secondary" (click)="verHistorial()">📋 Ver historial</button>
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
      background: #1b5e20; color: #fff; padding: 8px 18px; border-radius: 6px;
      border: none; cursor: pointer; text-decoration: none; font-size: 0.9rem; font-weight: 600; display: inline-block;
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
