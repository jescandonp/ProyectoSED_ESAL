import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { FirmanteDto } from '../../../core/models/esal.model';

interface FirmanteForm {
  nombre: string;
  cargo: string;
  dependencia: string;
  fechaInicioVigencia: string;
  fechaFinVigencia: string;
}

@Component({
  selector: 'app-firmantes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div>
      <h2 class="sed-page-title">Administración de Firmantes</h2>

      <!-- Formulario -->
      <div class="sed-card" style="margin-bottom: 16px;">
        <h3 style="font-size: 15px; font-weight: 600; margin-bottom: 16px; color: var(--color-primary);">
          {{ editandoId() ? 'Editar Firmante' : 'Nuevo Firmante' }}
        </h3>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
          <div class="sed-field">
            <label>Nombre *</label>
            <input type="text" class="sed-input" [(ngModel)]="form.nombre" placeholder="Nombre completo" />
          </div>
          <div class="sed-field">
            <label>Cargo *</label>
            <input type="text" class="sed-input" [(ngModel)]="form.cargo" placeholder="Cargo institucional" />
          </div>
          <div class="sed-field">
            <label>Dependencia</label>
            <input type="text" class="sed-input" [(ngModel)]="form.dependencia" placeholder="Dependencia" />
          </div>
          <div class="sed-field">
            <label>Inicio Vigencia *</label>
            <input type="date" class="sed-input" [(ngModel)]="form.fechaInicioVigencia" />
          </div>
          <div class="sed-field">
            <label>Fin Vigencia (vacío = indefinido)</label>
            <input type="date" class="sed-input" [(ngModel)]="form.fechaFinVigencia" />
          </div>
        </div>
        @if (errorForm()) {
          <div style="color: var(--color-error); font-size: 13px; margin-top: 8px;">{{ errorForm() }}</div>
        }
        <div style="display: flex; gap: 8px; margin-top: 16px;">
          <button class="sed-btn-primary" (click)="guardar()" [disabled]="guardando()">
            {{ guardando() ? 'Guardando...' : (editandoId() ? 'Actualizar' : 'Crear') }}
          </button>
          @if (editandoId()) {
            <button class="sed-btn-secondary" (click)="cancelarEdicion()">Cancelar</button>
          }
        </div>
      </div>

      <!-- Lista -->
      <div class="sed-card" style="padding: 0; overflow: hidden;">
        <table class="sed-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Cargo</th>
              <th>Inicio</th>
              <th>Fin</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            @if (firmantes().length === 0) {
              <tr><td colspan="6" style="text-align: center; color: var(--color-on-surface-variant); padding: 32px;">No hay firmantes registrados.</td></tr>
            }
            @for (f of firmantes(); track f.id) {
              <tr>
                <td style="font-weight: 600;">{{ f.nombre }}</td>
                <td>{{ f.cargo }}</td>
                <td>{{ f.fechaInicioVigencia | date:'dd/MM/yyyy' }}</td>
                <td>{{ f.fechaFinVigencia ? (f.fechaFinVigencia | date:'dd/MM/yyyy') : 'Indefinido' }}</td>
                <td>
                  <span [class]="f.activo ? 'sed-chip sed-chip--activo' : 'sed-chip sed-chip--cancelado'">
                    {{ f.activo ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td style="display: flex; gap: 4px; flex-wrap: wrap;">
                  <button class="sed-btn-secondary" style="padding: 4px 8px; font-size: 12px;" (click)="editar(f)">Editar</button>
                  @if (f.activo) {
                    <button class="sed-btn-secondary" style="padding: 4px 8px; font-size: 12px;" (click)="inactivar(f.id)">Inactivar</button>
                  } @else {
                    <button class="sed-btn-secondary" style="padding: 4px 8px; font-size: 12px;" (click)="activar(f.id)">Activar</button>
                  }
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
})
export class FirmantesComponent implements OnInit {
  private readonly api = inject(ApiService);

  firmantes  = signal<FirmanteDto[]>([]);
  editandoId = signal<number | null>(null);
  guardando  = signal(false);
  errorForm  = signal<string | null>(null);

  form: FirmanteForm = { nombre: '', cargo: '', dependencia: '', fechaInicioVigencia: '', fechaFinVigencia: '' };

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.get<FirmanteDto[]>('/api/admin/firmantes').subscribe({
      next: (list) => this.firmantes.set(list),
    });
  }

  guardar(): void {
    if (!this.form.nombre || !this.form.cargo || !this.form.fechaInicioVigencia) {
      this.errorForm.set('Nombre, cargo y fecha de inicio son obligatorios.');
      return;
    }
    this.errorForm.set(null);
    this.guardando.set(true);
    const body = {
      nombre: this.form.nombre,
      cargo: this.form.cargo,
      dependencia: this.form.dependencia || null,
      fechaInicioVigencia: this.form.fechaInicioVigencia,
      fechaFinVigencia: this.form.fechaFinVigencia || null,
    };
    const req = this.editandoId()
      ? this.api.put<FirmanteDto>(`/api/admin/firmantes/${this.editandoId()}`, body)
      : this.api.post<FirmanteDto>('/api/admin/firmantes', body);
    req.subscribe({
      next: () => { this.guardando.set(false); this.resetForm(); this.cargar(); },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(err?.error?.message ?? 'Error al guardar el firmante.');
      },
    });
  }

  editar(f: FirmanteDto): void {
    this.editandoId.set(f.id);
    this.form = {
      nombre: f.nombre,
      cargo: f.cargo,
      dependencia: f.dependencia ?? '',
      fechaInicioVigencia: f.fechaInicioVigencia,
      fechaFinVigencia: f.fechaFinVigencia ?? '',
    };
  }

  cancelarEdicion(): void { this.resetForm(); }

  activar(id: number): void {
    this.api.put<FirmanteDto>(`/api/admin/firmantes/${id}/activar`, {}).subscribe({ next: () => this.cargar() });
  }

  inactivar(id: number): void {
    this.api.put<FirmanteDto>(`/api/admin/firmantes/${id}/inactivar`, {}).subscribe({ next: () => this.cargar() });
  }

  private resetForm(): void {
    this.editandoId.set(null);
    this.form = { nombre: '', cargo: '', dependencia: '', fechaInicioVigencia: '', fechaFinVigencia: '' };
    this.errorForm.set(null);
  }
}
