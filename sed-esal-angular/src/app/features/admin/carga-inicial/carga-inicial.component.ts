import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { ImportResultDto } from '../../../core/models/esal.model';

@Component({
  selector: 'app-carga-inicial',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h2 class="sed-page-title">Carga Inicial</h2>

      <!-- Importar Base Histórica -->
      <div class="sed-card" style="margin-bottom: 24px;">
        <h3 style="font-size: 16px; font-weight: 600; color: var(--color-primary); margin-bottom: 16px;">
          📤 Importar Base Histórica de ESALes
        </h3>
        <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-bottom: 16px;">
          Seleccione el archivo Excel <strong>BASE DE DATOS - REGISTRO_1.xlsx</strong> para importar la base histórica de ESALes.
        </p>

        <div class="sed-field" style="margin-bottom: 16px;">
          <label>Archivo Excel (.xlsx)</label>
          <input
            type="file"
            accept=".xlsx"
            class="sed-input"
            (change)="onArchivoImportacion($event)"
            [disabled]="cargandoImportacion()"
          />
        </div>

        @if (errorImportacion()) {
          <div class="sed-error-msg" style="margin-bottom: 12px;">
            ⚠️ {{ errorImportacion() }}
          </div>
        }

        <button
          class="sed-btn-primary"
          (click)="cargarBaseHistorica()"
          [disabled]="!archivoImportacion() || cargandoImportacion()"
        >
          @if (cargandoImportacion()) {
            <span class="spinner"></span> Cargando...
          } @else {
            Cargar Base Histórica
          }
        </button>

        @if (resultadoImportacion()) {
          <div class="resultado-card" style="margin-top: 20px;">
            <h4 style="font-size: 14px; font-weight: 600; color: var(--color-primary); margin-bottom: 12px;">
              ✅ Importación completada
            </h4>
            <div class="resultado-grid">
              <div class="resultado-item">
                <span class="resultado-label">Leídos</span>
                <span class="resultado-valor">{{ resultadoImportacion()!.totalLeidos }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Importados</span>
                <span class="resultado-valor resultado-valor--ok">{{ resultadoImportacion()!.totalImportados }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Rechazados</span>
                <span class="resultado-valor resultado-valor--warn">{{ resultadoImportacion()!.totalRechazados }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Advertencias</span>
                <span class="resultado-valor resultado-valor--warn">{{ resultadoImportacion()!.totalAdvertencias }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Reformas</span>
                <span class="resultado-valor">{{ resultadoImportacion()!.totalReformas }}</span>
              </div>
            </div>
            <p style="font-size: 12px; color: var(--color-on-surface-variant); margin-top: 8px;">
              Importado por: {{ resultadoImportacion()!.importadoPor }}
            </p>
          </div>
        }
      </div>

      <!-- Inicializar Diccionario -->
      <div class="sed-card">
        <h3 style="font-size: 16px; font-weight: 600; color: var(--color-primary); margin-bottom: 16px;">
          📚 Inicializar Diccionario de Obligatoriedad
        </h3>
        <p style="font-size: 13px; color: var(--color-on-surface-variant); margin-bottom: 16px;">
          Seleccione el archivo Excel <strong>Base excel.xlsx</strong> para cargar o actualizar el diccionario de campos obligatorios.
        </p>

        <div class="sed-field" style="margin-bottom: 16px;">
          <label>Archivo Excel (.xlsx)</label>
          <input
            type="file"
            accept=".xlsx"
            class="sed-input"
            (change)="onArchivoDiccionario($event)"
            [disabled]="cargandoDiccionario()"
          />
        </div>

        @if (errorDiccionario()) {
          <div class="sed-error-msg" style="margin-bottom: 12px;">
            ⚠️ {{ errorDiccionario() }}
          </div>
        }

        <button
          class="sed-btn-primary"
          (click)="inicializarDiccionario()"
          [disabled]="!archivoDiccionario() || cargandoDiccionario()"
        >
          @if (cargandoDiccionario()) {
            <span class="spinner"></span> Cargando...
          } @else {
            Inicializar Diccionario
          }
        </button>

        @if (resultadoDiccionario()) {
          <div class="resultado-card" style="margin-top: 20px;">
            <h4 style="font-size: 14px; font-weight: 600; color: var(--color-primary); margin-bottom: 12px;">
              ✅ Diccionario inicializado
            </h4>
            <div class="resultado-grid">
              <div class="resultado-item">
                <span class="resultado-label">Leídos</span>
                <span class="resultado-valor">{{ resultadoDiccionario()!.totalLeidos }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Persistidos</span>
                <span class="resultado-valor resultado-valor--ok">{{ resultadoDiccionario()!.totalPersistidos }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Obligatorios</span>
                <span class="resultado-valor">{{ resultadoDiccionario()!.totalObligatorios }}</span>
              </div>
              <div class="resultado-item">
                <span class="resultado-label">Opcionales</span>
                <span class="resultado-valor">{{ resultadoDiccionario()!.totalOpcionales }}</span>
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .resultado-card {
      background-color: var(--color-surface-container-low);
      border: 1px solid var(--color-outline-variant);
      border-radius: var(--radius-lg);
      padding: 16px;
    }
    .resultado-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
    }
    .resultado-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      min-width: 80px;
    }
    .resultado-label {
      font-size: 11px;
      font-weight: 600;
      color: var(--color-on-surface-variant);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .resultado-valor {
      font-size: 24px;
      font-weight: 700;
      color: var(--color-primary);
      margin-top: 4px;
    }
    .resultado-valor--ok {
      color: #155724;
    }
    .resultado-valor--warn {
      color: #856404;
    }
    .spinner {
      display: inline-block;
      width: 14px;
      height: 14px;
      border: 2px solid rgba(255,255,255,0.4);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `],
})
export class CargaInicialComponent {
  private readonly api = inject(ApiService);

  // Importación
  archivoImportacion = signal<File | null>(null);
  cargandoImportacion = signal(false);
  errorImportacion = signal<string | null>(null);
  resultadoImportacion = signal<ImportResultDto | null>(null);

  // Diccionario
  archivoDiccionario = signal<File | null>(null);
  cargandoDiccionario = signal(false);
  errorDiccionario = signal<string | null>(null);
  resultadoDiccionario = signal<any | null>(null);

  onArchivoImportacion(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.archivoImportacion.set(input.files?.[0] ?? null);
    this.errorImportacion.set(null);
    this.resultadoImportacion.set(null);
  }

  onArchivoDiccionario(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.archivoDiccionario.set(input.files?.[0] ?? null);
    this.errorDiccionario.set(null);
    this.resultadoDiccionario.set(null);
  }

  cargarBaseHistorica(): void {
    const archivo = this.archivoImportacion();
    if (!archivo) return;

    const formData = new FormData();
    formData.append('archivo', archivo);

    this.cargandoImportacion.set(true);
    this.errorImportacion.set(null);

    this.api.postForm<ImportResultDto>('/api/admin/importaciones/esal', formData).subscribe({
      next: (result) => {
        this.resultadoImportacion.set(result);
        this.cargandoImportacion.set(false);
      },
      error: (err) => {
        this.errorImportacion.set(
          err?.error?.message ?? 'Error al importar el archivo. Verifique el formato.'
        );
        this.cargandoImportacion.set(false);
      },
    });
  }

  inicializarDiccionario(): void {
    const archivo = this.archivoDiccionario();
    if (!archivo) return;

    const formData = new FormData();
    formData.append('archivo', archivo);

    this.cargandoDiccionario.set(true);
    this.errorDiccionario.set(null);

    this.api.postForm<any>('/api/admin/diccionario/inicializar', formData).subscribe({
      next: (result) => {
        this.resultadoDiccionario.set(result);
        this.cargandoDiccionario.set(false);
      },
      error: (err) => {
        this.errorDiccionario.set(
          err?.error?.message ?? 'Error al inicializar el diccionario. Verifique el formato.'
        );
        this.cargandoDiccionario.set(false);
      },
    });
  }
}
