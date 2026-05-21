import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Ausencia {
  id: number;
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}

export interface AusenciaAdmin {
  id: number;
  tipo: string;
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

export interface SolicitudAusencia {
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
}

@Injectable({
  providedIn: 'root',
})
export class AusenciaService {
  private apiUrl = `${environment.apiUrl}/ausencias`;

  constructor(private http: HttpClient) {}

  // Mis ausencias
  getMisAusencias(): Observable<Ausencia[]> {
    return this.http.get<Ausencia[]>(`${this.apiUrl}/mis-ausencias`);
  }

  // Tipos de ausencia (vacaciones, medico, permiso, otros)
  getTiposAusencia(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/tipos`);
  }

  // Enviar solicitud de ausencia
  solicitarAusencia(solicitud: SolicitudAusencia): Observable<Ausencia> {
    return this.http.post<Ausencia>(`${this.apiUrl}/solicitar`, solicitud);
  }

  // Obtener todas las ausencias (admin)
  getTodasAusencias(): Observable<AusenciaAdmin[]> {
    return this.http.get<AusenciaAdmin[]>(this.apiUrl);
  }

  // Aprobar una ausencia
  aprobarAusencia(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/aprobar`, {});
  }

  // Rechazar una ausencia
  rechazarAusencia(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/rechazar`, {});
  }
}
