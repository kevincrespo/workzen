import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Teletrabajo {
  id: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
  estado: string;
}

export interface SolicitudTeletrabajo {
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
}

export interface TeletrabajoAdmin {
  id: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  empleado: {
    id: number;
    nombre: string;
    apellido1: string;
    apellido2: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class TeletrabajoService {
  private apiUrl = `${environment.apiUrl}/teletrabajos`;

  constructor(private http: HttpClient) {}

  // Mis teletrabajos
  getMisTeletrabajos(): Observable<Teletrabajo[]> {
    return this.http.get<Teletrabajo[]>(`${this.apiUrl}/mis-teletrabajos`);
  }

  // Solicitar teletrabajo
  solicitarTeletrabajo(solicitud: SolicitudTeletrabajo): Observable<Teletrabajo> {
    return this.http.post<Teletrabajo>(`${this.apiUrl}/solicitar`, solicitud);
  }

  // Pendientes (admin/rrhh)
  getTeletrabajosPendientes(): Observable<TeletrabajoAdmin[]> {
    return this.http.get<TeletrabajoAdmin[]>(`${this.apiUrl}/admin/pendientes`);
  }

  // Aprobar
  aprobarTeletrabajo(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/aprobar`, {});
  }

  // Rechazar
  rechazarTeletrabajo(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/rechazar`, {});
  }
}
