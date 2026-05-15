import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-admin-esales-detail',
  standalone: true,
  template: `
    <div>
      <h2 class="sed-page-title">Detalle ESAL - Administración</h2>
      <div class="sed-card">
        <p>ID: {{ id }}</p>
        <p style="margin-top: 16px; color: var(--color-on-surface-variant);"><em>En desarrollo - I1</em></p>
      </div>
    </div>
  `,
})
export class AdminEsalesDetailComponent {
  private readonly route = inject(ActivatedRoute);
  readonly id = this.route.snapshot.paramMap.get('id') ?? '';
}
