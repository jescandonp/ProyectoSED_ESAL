import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import {
  EstadoEsal,
  MantenimientoEsalDto,
  NombramientoDto,
  OrganoAdministracionDto,
} from '../../../core/models/esal.model';

type Seccion = 'principal' | 'personeria' | 'representantes' | 'organo' | 'estado';

@Component({
  selector: 'app-esal-maintenance',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="maintenance">
      <div class="maintenance__header">
        <button class="sed-btn-secondary" type="button" (click)="volver()">Volver</button>
        <div class="maintenance__title">
          <h2 class="sed-page-title">Mantenimiento ESAL</h2>
          @if (mantenimiento()) {
            <p>{{ mantenimiento()!.informacionPrincipal.nombre || 'Sin nombre' }}</p>
          }
        </div>
        @if (mantenimiento()) {
          <span [class]="'sed-chip ' + chipEstado(estadoActual())">{{ labelEstado(estadoActual()) }}</span>
        }
      </div>

      @if (cargando()) {
        <div class="sed-card maintenance__empty">Cargando mantenimiento...</div>
      } @else if (error()) {
        <div class="sed-card maintenance__error">{{ error() }}</div>
      } @else if (mantenimiento()) {
        @if (estaCancelada()) {
          <div class="sed-card maintenance__warning">
            La ESAL está cancelada. La edición ordinaria queda bloqueada; use la sección Estado para reactivar.
          </div>
        }

        <div class="maintenance__layout">
          <nav class="maintenance__nav" aria-label="Secciones de mantenimiento">
            <button type="button" [class.active]="seccion() === 'principal'" (click)="seccion.set('principal')">Información principal</button>
            <button type="button" [class.active]="seccion() === 'personeria'" (click)="seccion.set('personeria')">Constitución y personería</button>
            <button type="button" [class.active]="seccion() === 'representantes'" (click)="seccion.set('representantes')">Representante legal</button>
            <button type="button" [class.active]="seccion() === 'organo'" (click)="seccion.set('organo')">Órgano de administración</button>
            <button type="button" [class.active]="seccion() === 'estado'" (click)="seccion.set('estado')">Estado y cancelación</button>
          </nav>

          <section class="maintenance__content">
            @if (mensaje()) {
              <div class="maintenance__ok">{{ mensaje() }}</div>
            }
            @if (errorAccion()) {
              <div class="maintenance__error">{{ errorAccion() }}</div>
            }

            @if (seccion() === 'principal') {
              <form class="sed-card maintenance__section" [formGroup]="principalForm" (ngSubmit)="guardarPrincipal()">
                <div class="maintenance__section-head">
                  <h3>Información principal</h3>
                  <button class="sed-btn-primary" type="submit" [disabled]="guardando() || estaCancelada()">Guardar sección</button>
                </div>
                <div class="maintenance__grid">
                  <label class="sed-field">Nombre<input class="sed-input" formControlName="nombre" /></label>
                  <label class="sed-field">ID SIPEJ<input class="sed-input" formControlName="idSipej" /></label>
                  <label class="sed-field">NIT<input class="sed-input" formControlName="nit" /></label>
                  <label class="sed-field">Domicilio<input class="sed-input" formControlName="domicilio" /></label>
                  <label class="sed-field">Correo electrónico<input class="sed-input" formControlName="correoElectronico" /></label>
                  <label class="sed-field">Término de duración<input class="sed-input" formControlName="terminoDuracion" /></label>
                </div>
                <label class="sed-field">Objeto social<textarea class="sed-input" formControlName="objetoSocial" rows="4"></textarea></label>
              </form>
            }

            @if (seccion() === 'personeria') {
              <form class="sed-card maintenance__section" [formGroup]="personeriaForm" (ngSubmit)="guardarPersoneria()">
                <div class="maintenance__section-head">
                  <h3>Constitución y personería jurídica</h3>
                  <button class="sed-btn-primary" type="submit" [disabled]="guardando() || estaCancelada()">Guardar sección</button>
                </div>
                <div class="maintenance__grid">
                  <label class="sed-field">Reconocimiento<input class="sed-input" formControlName="reconocimientoPersoneriaJuridica" /></label>
                  <label class="sed-field">Fecha reconocimiento<input class="sed-input" type="date" formControlName="fechaReconocimientoPersoneriaJuridica" /></label>
                  <label class="sed-field">Entidad que expide<input class="sed-input" formControlName="entidadQueExpide" /></label>
                  <label class="sed-field">Inscripción<input class="sed-input" formControlName="inscripcion" /></label>
                  <label class="sed-field">Fecha inscripción<input class="sed-input" type="date" formControlName="fechaInscripcion" /></label>
                  <label class="sed-field">Entidad que inscribió<input class="sed-input" formControlName="entidadQueInscribio" /></label>
                </div>
              </form>
            }

            @if (seccion() === 'representantes') {
              <div class="sed-card maintenance__section">
                <div class="maintenance__section-head">
                  <h3>Representantes legales</h3>
                  <button class="sed-btn-secondary" type="button" (click)="nuevoRepresentante()" [disabled]="estaCancelada()">Nuevo</button>
                </div>
                <table class="sed-table">
                  <thead><tr><th>Tipo</th><th>Nombre</th><th>Documento</th><th>Cargo</th><th>Vigente</th><th>Acción</th></tr></thead>
                  <tbody>
                    @for (rep of mantenimiento()!.representantes; track rep.id) {
                      <tr>
                        <td>{{ rep.tipoNombramiento }}</td>
                        <td>{{ rep.nombre }}</td>
                        <td>{{ rep.tipoDocumento || '—' }} {{ rep.numeroDocumento || '' }}</td>
                        <td>{{ rep.cargo || '—' }}</td>
                        <td>{{ rep.vigente ? 'Sí' : 'No' }}</td>
                        <td><button class="sed-btn-secondary compact" type="button" (click)="editarRepresentante(rep)" [disabled]="estaCancelada()">Editar</button></td>
                      </tr>
                    }
                  </tbody>
                </table>
                <form class="maintenance__subform" [formGroup]="representanteForm" (ngSubmit)="guardarRepresentante()">
                  <div class="maintenance__grid">
                    <label class="sed-field">Tipo<select class="sed-input" formControlName="tipoNombramiento"><option value="REPRESENTANTE_LEGAL">Principal</option><option value="REPRESENTANTE_LEGAL_SUPLENTE">Suplente</option></select></label>
                    <label class="sed-field">Nombre<input class="sed-input" formControlName="nombre" /></label>
                    <label class="sed-field">Tipo documento<input class="sed-input" formControlName="tipoDocumento" /></label>
                    <label class="sed-field">Número documento<input class="sed-input" formControlName="numeroDocumento" /></label>
                    <label class="sed-field">Cargo<input class="sed-input" formControlName="cargo" /></label>
                    <label class="sed-field">Acta aprueba<input class="sed-input" formControlName="actaAprueba" /></label>
                    <label class="sed-field">Fecha acta<input class="sed-input" type="date" formControlName="fechaActa" /></label>
                    <label class="sed-field">Vigente<select class="sed-input" formControlName="vigente"><option [ngValue]="true">Sí</option><option [ngValue]="false">No</option></select></label>
                  </div>
                  <label class="sed-field">Facultades o limitaciones<textarea class="sed-input" formControlName="facultadesLimitaciones" rows="3"></textarea></label>
                  <button class="sed-btn-primary" type="submit" [disabled]="guardando() || estaCancelada()">Guardar representante</button>
                </form>
              </div>
            }

            @if (seccion() === 'organo') {
              <div class="sed-card maintenance__section">
                <div class="maintenance__section-head">
                  <h3>Órgano de administración</h3>
                  <button class="sed-btn-secondary" type="button" (click)="nuevoOrgano()" [disabled]="estaCancelada()">Nuevo</button>
                </div>
                <table class="sed-table">
                  <thead><tr><th>Órgano</th><th>Miembro</th><th>Cargo</th><th>Documento</th><th>Acta</th><th>Acción</th></tr></thead>
                  <tbody>
                    @for (org of mantenimiento()!.organosAdministracion; track org.id) {
                      <tr>
                        <td>{{ org.organo || '—' }}</td>
                        <td>{{ org.miembro || '—' }}</td>
                        <td>{{ org.cargo || '—' }}</td>
                        <td>{{ org.tipoDocumento || '—' }} {{ org.numeroDocumento || '' }}</td>
                        <td>{{ org.actaAprueba || '—' }}</td>
                        <td><button class="sed-btn-secondary compact" type="button" (click)="editarOrgano(org)" [disabled]="estaCancelada()">Editar</button></td>
                      </tr>
                    }
                  </tbody>
                </table>
                <form class="maintenance__subform" [formGroup]="organoForm" (ngSubmit)="guardarOrgano()">
                  <div class="maintenance__grid">
                    <label class="sed-field">Órgano<input class="sed-input" formControlName="organo" /></label>
                    <label class="sed-field">Miembro<input class="sed-input" formControlName="miembro" /></label>
                    <label class="sed-field">Cargo<input class="sed-input" formControlName="cargo" /></label>
                    <label class="sed-field">Tipo documento<input class="sed-input" formControlName="tipoDocumento" /></label>
                    <label class="sed-field">Número documento<input class="sed-input" formControlName="numeroDocumento" /></label>
                    <label class="sed-field">Acta aprueba<input class="sed-input" formControlName="actaAprueba" /></label>
                    <label class="sed-field">Fecha acta<input class="sed-input" type="date" formControlName="fechaActa" /></label>
                    <label class="sed-field">Acta aclaratoria<input class="sed-input" formControlName="actaAclaratoria" /></label>
                    <label class="sed-field">Fecha acta aclaratoria<input class="sed-input" type="date" formControlName="fechaActaAclaratoria" /></label>
                  </div>
                  <label class="sed-field">Facultades o limitaciones<textarea class="sed-input" formControlName="facultadesLimitaciones" rows="3"></textarea></label>
                  <button class="sed-btn-primary" type="submit" [disabled]="guardando() || estaCancelada()">Guardar miembro</button>
                </form>
              </div>
            }

            @if (seccion() === 'estado') {
              <div class="sed-card maintenance__section">
                <h3>Estado y cancelación</h3>
                @if (!estaCancelada()) {
                  <form class="maintenance__subform" [formGroup]="cancelacionForm" (ngSubmit)="cancelar()">
                    <div class="maintenance__grid">
                      <label class="sed-field">Resolución<input class="sed-input" formControlName="resolucion" /></label>
                      <label class="sed-field">Fecha de cancelación<input class="sed-input" type="date" formControlName="fechaResolucion" /></label>
                    </div>
                    <label class="sed-field">Motivo<textarea class="sed-input" formControlName="motivo" rows="3"></textarea></label>
                    <button class="sed-btn-primary danger" type="submit" [disabled]="guardando()">Registrar cancelación</button>
                  </form>
                } @else {
                  <form class="maintenance__subform" [formGroup]="reactivacionForm" (ngSubmit)="reactivar()">
                    <div class="maintenance__grid">
                      <label class="sed-field">Estado destino<select class="sed-input" formControlName="estadoDestino"><option value="ACTIVO">Activo</option><option value="SUSPENDIDO">Suspendido</option><option value="EN_LIQUIDACION">En liquidación</option></select></label>
                    </div>
                    <label class="sed-field">Motivo<textarea class="sed-input" formControlName="motivo" rows="3"></textarea></label>
                    <button class="sed-btn-primary" type="submit" [disabled]="guardando()">Reactivar ESAL</button>
                  </form>
                }
              </div>
            }
          </section>
        </div>
      }
    </div>
  `,
  styles: [`
    .maintenance__header { display: flex; align-items: center; gap: 16px; margin-bottom: 16px; }
    .maintenance__title { flex: 1; }
    .maintenance__title p { color: var(--color-on-surface-variant); font-size: 13px; }
    .maintenance__layout { display: grid; grid-template-columns: 240px 1fr; gap: 16px; align-items: start; }
    .maintenance__nav { display: flex; flex-direction: column; gap: 4px; }
    .maintenance__nav button { text-align: left; padding: 10px 12px; border: 1px solid var(--color-outline-variant); background: var(--color-surface); border-radius: var(--radius-default); cursor: pointer; color: var(--color-on-surface); }
    .maintenance__nav button.active { border-color: var(--color-primary-container); color: var(--color-primary-container); font-weight: 700; background: var(--color-surface-container-low); }
    .maintenance__content { min-width: 0; }
    .maintenance__section { display: flex; flex-direction: column; gap: 16px; }
    .maintenance__section h3 { color: var(--color-primary); font-size: 17px; }
    .maintenance__section-head { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
    .maintenance__grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
    .maintenance__subform { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--color-outline-variant); }
    .maintenance__warning { margin-bottom: 16px; color: #856404; background: #fff8db; }
    .maintenance__ok { margin-bottom: 12px; color: #155724; background: #d4edda; padding: 10px 12px; border-radius: var(--radius-default); }
    .maintenance__error { margin-bottom: 12px; color: var(--color-error); background: var(--color-error-container); padding: 10px 12px; border-radius: var(--radius-default); }
    .maintenance__empty { color: var(--color-on-surface-variant); text-align: center; }
    .compact { padding: 4px 10px; font-size: 12px; }
    .danger { background: var(--color-error); }
    @media (max-width: 900px) {
      .maintenance__layout { grid-template-columns: 1fr; }
      .maintenance__grid { grid-template-columns: 1fr; }
      .maintenance__header { align-items: flex-start; flex-direction: column; }
    }
  `],
})
export class EsalMaintenanceComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly id = this.route.snapshot.paramMap.get('id') ?? '';
  mantenimiento = signal<MantenimientoEsalDto | null>(null);
  cargando = signal(false);
  guardando = signal(false);
  error = signal<string | null>(null);
  errorAccion = signal<string | null>(null);
  mensaje = signal<string | null>(null);
  seccion = signal<Seccion>('principal');
  representanteEditando = signal<number | null>(null);
  organoEditando = signal<number | null>(null);

  principalForm = this.fb.group({
    nombre: [''],
    idSipej: [''],
    nit: [''],
    domicilio: [''],
    correoElectronico: [''],
    terminoDuracion: [''],
    objetoSocial: [''],
  });

  personeriaForm = this.fb.group({
    reconocimientoPersoneriaJuridica: [''],
    fechaReconocimientoPersoneriaJuridica: [''],
    entidadQueExpide: [''],
    inscripcion: [''],
    fechaInscripcion: [''],
    entidadQueInscribio: [''],
  });

  representanteForm = this.fb.group({
    tipoNombramiento: ['REPRESENTANTE_LEGAL'],
    nombre: [''],
    tipoDocumento: [''],
    numeroDocumento: [''],
    cargo: [''],
    actaAprueba: [''],
    fechaActa: [''],
    facultadesLimitaciones: [''],
    vigente: [true],
  });

  organoForm = this.fb.group({
    organo: [''],
    miembro: [''],
    cargo: [''],
    tipoDocumento: [''],
    numeroDocumento: [''],
    actaAprueba: [''],
    fechaActa: [''],
    actaAclaratoria: [''],
    fechaActaAclaratoria: [''],
    facultadesLimitaciones: [''],
  });

  cancelacionForm = this.fb.group({
    resolucion: [''],
    fechaResolucion: [''],
    motivo: [''],
  });

  reactivacionForm = this.fb.group({
    estadoDestino: ['ACTIVO'],
    motivo: [''],
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.api.get<MantenimientoEsalDto>(`/api/esales/${this.id}/mantenimiento`).subscribe({
      next: (data) => {
        this.mantenimiento.set(data);
        this.hidratarFormularios(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la vista de mantenimiento.');
        this.cargando.set(false);
      },
    });
  }

  volver(): void {
    this.router.navigate(['/admin/esales', this.id]);
  }

  estadoActual(): EstadoEsal {
    return this.mantenimiento()?.informacionPrincipal.estado ?? 'ACTIVO';
  }

  estaCancelada(): boolean {
    return this.estadoActual() === 'CANCELADO';
  }

  guardarPrincipal(): void {
    this.mutar(() => this.api.put<MantenimientoEsalDto>(`/api/esales/${this.id}/informacion-principal`, this.principalForm.value), 'Información principal guardada.');
  }

  guardarPersoneria(): void {
    this.mutar(() => this.api.put<MantenimientoEsalDto>(`/api/esales/${this.id}/personeria-juridica`, this.personeriaForm.value), 'Personería jurídica guardada.');
  }

  guardarRepresentante(): void {
    const repId = this.representanteEditando();
    const request = repId
      ? () => this.api.put<NombramientoDto>(`/api/esales/${this.id}/representantes/${repId}`, this.representanteForm.value)
      : () => this.api.post<NombramientoDto>(`/api/esales/${this.id}/representantes`, this.representanteForm.value);
    this.mutarLista(request, 'Representante guardado.');
  }

  guardarOrgano(): void {
    const miembroId = this.organoEditando();
    const request = miembroId
      ? () => this.api.put<OrganoAdministracionDto>(`/api/esales/${this.id}/organos-administracion/${miembroId}`, this.organoForm.value)
      : () => this.api.post<OrganoAdministracionDto>(`/api/esales/${this.id}/organos-administracion`, this.organoForm.value);
    this.mutarLista(request, 'Miembro de órgano guardado.');
  }

  cancelar(): void {
    this.mutar(() => this.api.post<MantenimientoEsalDto>(`/api/esales/${this.id}/cancelacion`, this.cancelacionForm.value), 'Cancelación registrada.');
  }

  reactivar(): void {
    this.mutar(() => this.api.post<MantenimientoEsalDto>(`/api/esales/${this.id}/reactivacion`, this.reactivacionForm.value), 'ESAL reactivada.');
  }

  nuevoRepresentante(): void {
    this.representanteEditando.set(null);
    this.representanteForm.reset({ tipoNombramiento: 'REPRESENTANTE_LEGAL', vigente: true });
  }

  editarRepresentante(rep: NombramientoDto): void {
    this.representanteEditando.set(rep.id ?? null);
    this.representanteForm.patchValue(rep as any);
  }

  nuevoOrgano(): void {
    this.organoEditando.set(null);
    this.organoForm.reset();
  }

  editarOrgano(org: OrganoAdministracionDto): void {
    this.organoEditando.set(org.id ?? null);
    this.organoForm.patchValue(org as any);
  }

  private mutar(request: () => any, ok: string): void {
    this.guardando.set(true);
    this.mensaje.set(null);
    this.errorAccion.set(null);
    request().subscribe({
      next: (data: MantenimientoEsalDto) => {
        this.mantenimiento.set(data);
        this.hidratarFormularios(data);
        this.mensaje.set(ok);
        this.guardando.set(false);
      },
      error: (err: any) => {
        this.errorAccion.set(err?.error?.message ?? 'No se pudo guardar la sección.');
        this.guardando.set(false);
      },
    });
  }

  private mutarLista(request: () => any, ok: string): void {
    this.guardando.set(true);
    this.mensaje.set(null);
    this.errorAccion.set(null);
    request().subscribe({
      next: () => {
        this.mensaje.set(ok);
        this.guardando.set(false);
        this.cargar();
      },
      error: (err: any) => {
        this.errorAccion.set(err?.error?.message ?? 'No se pudo guardar el registro.');
        this.guardando.set(false);
      },
    });
  }

  private hidratarFormularios(data: MantenimientoEsalDto): void {
    this.principalForm.patchValue(data.informacionPrincipal as any);
    if (data.personeriaJuridica) {
      this.personeriaForm.patchValue(data.personeriaJuridica as any);
    }
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
      EN_LIQUIDACION: 'En liquidación',
      CANCELADO: 'Cancelado',
    };
    return map[estado] ?? estado;
  }
}
