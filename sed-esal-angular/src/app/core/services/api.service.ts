import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

/**
 * Credenciales hardcoded para el perfil local-dev.
 * Solo se usan para construir el header HTTP Basic.
 * En I4 se reemplazará por JWT Azure AD.
 */
const LOCAL_DEV_CREDENTIALS: Record<string, string> = {
  'admin@educacionbogota.edu.co': 'admin123',
  'expedidor@educacionbogota.edu.co': 'expedidor123',
};

/**
 * Servicio HTTP base para llamadas al backend SED_ESAL.
 * Centraliza la autenticación HTTP Basic y la URL base.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private getHeaders(): HttpHeaders {
    const user = this.auth.currentUser();
    if (!user) return new HttpHeaders();
    const password = LOCAL_DEV_CREDENTIALS[user.email] ?? '';
    const encoded = btoa(`${user.email}:${password}`);
    return new HttpHeaders({ Authorization: `Basic ${encoded}` });
  }

  get<T>(path: string, params?: Record<string, string | number>): Observable<T> {
    let httpParams = new HttpParams();
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }
    return this.http.get<T>(path, {
      headers: this.getHeaders(),
      params: httpParams,
    });
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(path, body, { headers: this.getHeaders() });
  }

  put<T>(path: string, body: unknown): Observable<T> {
    return this.http.put<T>(path, body, { headers: this.getHeaders() });
  }

  postForm<T>(path: string, formData: FormData): Observable<T> {
    // No incluir Content-Type — el browser lo establece automáticamente con boundary
    const user = this.auth.currentUser();
    let headers = new HttpHeaders();
    if (user) {
      const password = LOCAL_DEV_CREDENTIALS[user.email] ?? '';
      const encoded = btoa(`${user.email}:${password}`);
      headers = headers.set('Authorization', `Basic ${encoded}`);
    }
    return this.http.post<T>(path, formData, { headers });
  }

  download(path: string): Observable<Blob> {
    return this.http.get(path, {
      headers: this.getHeaders(),
      responseType: 'blob',
    });
  }
}
