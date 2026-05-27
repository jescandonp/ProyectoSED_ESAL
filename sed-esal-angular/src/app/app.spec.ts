import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { App } from './app';
import { routes } from './app.routes';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter(routes), provideHttpClient()],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render router-outlet', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('router-outlet')).toBeTruthy();
  });

  it('should expose I5 maintenance route only for administrators', () => {
    const shellRoute = routes.find((route) => route.component);
    const maintenanceRoute = shellRoute?.children?.find(
      (route) => route.path === 'admin/esales/:id/mantenimiento'
    );

    expect(maintenanceRoute).toBeTruthy();
    expect(maintenanceRoute?.canActivate).toBeTruthy();
  });

  it('should not keep stale busqueda route references in the router', () => {
    const shellRoute = routes.find((route) => route.component);
    const busquedaRoute = shellRoute?.children?.find(
      (route) => route.path === 'busqueda'
    );

    expect(busquedaRoute).toBeUndefined();
  });
});
