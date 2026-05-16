import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { CertificadoDto, EstadoCertificado } from '../../core/models/esal.model';

@Component({
  selector: 'app-historial-certificados',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
        <button class="sed-btn-secondary" style="padding: 6px 12px;" (click)="volver()">← Volver</button>
        <h2 class="sed-page-title" style="margin-bottom: 0;">Historial de Certificados</h2>
      </div>

      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">Cargando...</div>
      } @else if (error()) {
        <div class="sed-card" style="color: var(--color-error); padding: 24px; text-align: center;">{{ error() }}</div>
      } @else {
        <div class="sed-card" style="padding: 0; overflow: hidden;">
          <table class="sed-table">
            <thead>
              <tr>
                <th>Número</th>
                <th>Estado</th>
                <th>Fecha</th>
                <th>Firmante</th>
                <th>Hash</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @if (certificados().length === 0) {
                <tr><td colspan="6" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">Sin certificados generados.</td></tr>
              }
              @for (cert of certificados(); track cert.certificadoId) {
                <tr>
                  <td style="font-weight: 600;">{{ cert.numeroCertificado ?? '—' }}</td>
                  <td><span [class]="'sed-chip ' + chipEstado(cert.estadoCertificado)">{{ labelEstado(cert.estadoCertificado) }}</span></td>
                  <td>{{ cert.fechaExpedicion | date:'dd/MM/yyyy HH:mm' }}</td>
                  <td>{{ cert.firmanteNombre ?? '—' }}</td>
                  <td style="font-family: monospace; font-size: 11px;">{{ cert.hashSha256 ? cert.hashSha256.substring(0, 12) + '...' : '—' }}</td>
                  <td>
                    <button class="sed-btn-secondary" style="padding: 4px 10px; font-size: 12px;"
                            (click)="verDetalle(cert.certificadoId)">Ver</button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
})
export class HistorialCertificadosComponent implements OnInit {
  private readonly api    = inject(ApiService);
  private readonly route  = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly esalId = this.route.snapshot.paramMap.get('id') ?? '';

  certificados = signal<CertificadoDto[]>([]);
  cargando     = signal(false);
  error        = signal<string | null>(null);

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.get<CertificadoDto[]>(`/api/certificados/esales/${this.esalId}/historial`).subscribe({
      next: (list) => { this.certificados.set(list); this.cargando.set(false); },
      error: () => { this.error.set('Error al cargar el historial.'); this.cargando.set(false); },
    });
  }

  volver(): void { this.router.navigate(['/esales', this.esalId]); }
  verDetalle(id: number): void { this.router.navigate(['/certificados', id]); }

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
}
