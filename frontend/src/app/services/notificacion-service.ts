import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Notificacion } from '../models/notificacion';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  private apiUrl = `${environment.apiUrl}/notificaciones`;
  private http = inject(HttpClient);

  getNotificaciones(): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(this.apiUrl);
  }

  contarNoLeidas(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/no-leidas`);
  }

  marcarComoLeida(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/leer`, {});
  }

  marcarTodasComoLeidas(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/leer-todas`, {});
  }
}
