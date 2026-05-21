import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/** Dieta del empleado autenticado */
export interface Dieta {
  id: number;
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  motivo: string;
  estado: string;
}

/** Dieta con datos del empleado (vista admin/rrhh) */
export interface DietaAdmin {
  id: number;
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  motivo: string;
  estado: string;
  empleado: {
    id: number;
    nombre: string;
    apellido1: string;
    apellido2: string;
  };
}

/** Datos para solicitar una dieta */
export interface SolicitudDieta {
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  motivo: string;
}

@Injectable({
  providedIn: 'root',
})
export class DietaService {
  private apiUrl = `${environment.apiUrl}/dietas`;

  constructor(private http: HttpClient) {}

  /** Obtener las dietas del empleado autenticado */
  getMisDietas(): Observable<Dieta[]> {
    return this.http.get<Dieta[]>(`${this.apiUrl}/mis-dietas`);
  }

  /** Enviar solicitud de dieta */
  solicitarDieta(solicitud: SolicitudDieta): Observable<Dieta> {
    return this.http.post<Dieta>(`${this.apiUrl}/solicitar`, solicitud);
  }

  /** Obtener todas las dietas (admin/rrhh) */
  getTodasDietas(): Observable<DietaAdmin[]> {
    return this.http.get<DietaAdmin[]>(this.apiUrl);
  }

  /** Aprobar una dieta */
  aprobarDieta(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/aprobar`, {});
  }

  /** Rechazar una dieta */
  rechazarDieta(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/rechazar`, {});
  }
}
