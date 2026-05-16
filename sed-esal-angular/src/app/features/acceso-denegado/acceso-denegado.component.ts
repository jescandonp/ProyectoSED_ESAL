import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-acceso-denegado',
  standalone: true,
  template: `
    <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; gap: 24px; text-align: center; padding: 32px;">
      <div style="font-size: 48px;">🔒</div>
      <h2 class="sed-page-title">Acceso Denegado</h2>
      <p style="color: var(--color-on-surface-variant); max-width: 400px;">
        No tiene permisos para acceder a este recurso. Si cree que esto es un error, contacte al administrador del sistema.
      </p>
      <button class="sed-btn-secondary" (click)="volver()">← Volver al inicio</button>
    </div>
  `,
})
export class AccesoDenegadoComponent {
  private readonly router = inject(Router);

  volver(): void {
    this.router.navigate(['/dashboard']);
  }
}
