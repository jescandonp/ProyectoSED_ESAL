import { Injectable, inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

/**
 * Interceptor global para errores HTTP 401 y 403 (I4).
 * - 401: limpia la sesion y redirige al login.
 * - 403: redirige a la pantalla de acceso denegado.
 * Garantiza que los errores de red no expongan detalles tecnicos al usuario.
 */
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.auth.logout();
          this.router.navigate(['/login']);
        } else if (error.status === 403) {
          this.router.navigate(['/acceso-denegado']);
        }
        return throwError(() => error);
      })
    );
  }
}
