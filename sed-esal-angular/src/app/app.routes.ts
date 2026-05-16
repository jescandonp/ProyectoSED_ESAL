import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/auth/auth.guard';
import { ShellComponent } from './shared/layout/shell.component';

export const routes: Routes = [
  // Redirigir raíz a login
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Login (público)
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },

  // Rutas protegidas dentro del shell
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'admin/carga-inicial',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/carga-inicial/carga-inicial.component').then(
            (m) => m.CargaInicialComponent
          ),
      },
      {
        path: 'admin/esales',
        loadComponent: () =>
          import('./features/admin/esales/esales-list.component').then(
            (m) => m.AdminEsalesListComponent
          ),
      },
      {
        path: 'admin/esales/:id',
        loadComponent: () =>
          import('./features/admin/esales/esales-detail.component').then(
            (m) => m.AdminEsalesDetailComponent
          ),
      },
      {
        path: 'admin/auditoria',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/auditoria/auditoria.component').then(
            (m) => m.AuditoriaComponent
          ),
      },
      {
        path: 'esales',
        loadComponent: () =>
          import('./features/esales/esales-list.component').then(
            (m) => m.EsalesListComponent
          ),
      },
      {
        path: 'esales/:id',
        loadComponent: () =>
          import('./features/esales/esales-detail.component').then(
            (m) => m.EsalesDetailComponent
          ),
      },
      // I2 — Búsqueda operativa
      {
        path: 'busqueda',
        loadComponent: () =>
          import('./features/busqueda/busqueda.component').then(
            (m) => m.BusquedaComponent
          ),
      },
      {
        path: 'busqueda/:id',
        loadComponent: () =>
          import('./features/busqueda/busqueda-detalle.component').then(
            (m) => m.BusquedaDetalleComponent
          ),
      },
      // I2 — Vista previa del certificado
      {
        path: 'certificados/preview/:id',
        loadComponent: () =>
          import('./features/certificados/preview-certificado.component').then(
            (m) => m.PreviewCertificadoComponent
          ),
      },
    ],
  },

  // Fallback
  { path: '**', redirectTo: '/login' },
];
