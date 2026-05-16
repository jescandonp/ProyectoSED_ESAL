import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { NumeracionDto } from '../../../core/models/esal.model';

@Component({
  selector: 'app-numeracion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <h2 class="sed-page-title">Configuración de Numeración</h2>

      @if (cargando()) {
        <div style="text-align: center; padding: 40px; color: var(--color-on-surface-variant);">Cargando...</div>
      } @else if (numeracion()) {
        <div class="sed-card" style="margin-bottom: 16px;">
          <div class="info-grid" style="margin-bottom: 20px;">
            <div class="info-item"><span class="info-label">Año</span><span class="info-valor">{{ numeracion()!.anio }}</span></div>
            <div class="info-item"><span class="info-label">Prefijo actual</span><span class="info-valor">{{ numeracion()!.prefijo }}</span></div>
            <div class="info-item"><span class="info-label">Último consecutivo</span><span class="info-valor">{{ numeracion()!.ultimoConsecutivo }}</span></div>
            <div class="info-item"><span class="info-label">Próximo número</span>
              <span class="info-valor" style="font-weight: 700; color: var(--color-primary);">
                {{ numeracion()!.prefijo }}-{{ numeracion()!.anio }}-{{ formatConsecutivo(numeracion()!.ultimoConsecutivo + 1) }}
              </span>
            </div>
          </div>

          <h4 style="font-size: 14px; font-weight: 600; margin-bottom: 12px;">Actualizar prefijo</h4>
          <div style="display: flex; gap: 12px; align-items: flex-end;">
            <div class="sed-field" style="min-width: 160px;">
              <label>Nuevo prefijo</label>
              <input type="text" class="sed-input" [(ngModel)]="nuevoPrefijo"
                     placeholder="{{ numeracion()!.prefijo }}" style="text-transform: uppercase;" />
            </div>
            <button class="sed-btn-primary" (click)="actualizar()" [disabled]="guardando()">
              {{ guardando() ? 'Guardando...' : 'Actualizar' }}
            </button>
          </div>
          @if (mensaje()) {
            <p style="color: #155724; font-size: 13px; margin-top: 8px;">✅ {{ mensaje() }}</p>
          }
          @if (errorMsg()) {
            <p style="color: var(--color-error); font-size: 13px; margin-top: 8px;">⚠️ {{ errorMsg() }}</p>
          }
        </div>

        <div class="sed-card" style="background: #f0f4ff; border-left: 4px solid #3b82f6;">
          <p style="font-size: 13px; color: #1e40af;">
            <strong>Nota:</strong> El consecutivo se reinicia automáticamente al inicio de cada año.
            El prefijo se puede cambiar, pero los certificados ya emitidos conservan su número original.
          </p>
        </div>
      }
    </div>
  `,
  styles: [`
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .info-item { display: flex; flex-direction: column; gap: 4px; }
    .info-label { font-size: 11px; font-weight: 600; color: var(--color-on-surface-variant); text-transform: uppercase; letter-spacing: 0.05em; }
    .info-valor { font-size: 14px; color: var(--color-on-surface); }
  `],
})
export class NumeracionComponent implements OnInit {
  private readonly api = inject(ApiService);

  numeracion  = signal<NumeracionDto | null>(null);
  cargando    = signal(false);
  guardando   = signal(false);
  nuevoPrefijo = '';
  mensaje     = signal<string | null>(null);
  errorMsg    = signal<string | null>(null);

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.get<NumeracionDto>('/api/admin/certificados/numeracion').subscribe({
      next: (n) => { this.numeracion.set(n); this.cargando.set(false); },
      error: () => this.cargando.set(false),
    });
  }

  actualizar(): void {
    if (!this.nuevoPrefijo.trim()) { this.errorMsg.set('Ingrese un prefijo válido.'); return; }
    this.errorMsg.set(null);
    this.guardando.set(true);
    this.api.put<NumeracionDto>('/api/admin/certificados/numeracion', { prefijo: this.nuevoPrefijo.trim() }).subscribe({
      next: (n) => {
        this.numeracion.set(n);
        this.nuevoPrefijo = '';
        this.guardando.set(false);
        this.mensaje.set('Prefijo actualizado correctamente.');
        setTimeout(() => this.mensaje.set(null), 3000);
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorMsg.set(err?.error?.message ?? 'Error al actualizar.');
      },
    });
  }

  formatConsecutivo(n: number): string {
    return String(n).padStart(6, '0');
  }
}
