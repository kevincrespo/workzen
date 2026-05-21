import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/** Pausa dentro de un fichaje */
export interface Pausa {
  id: number;
  horaInicio: string;
  horaFin: string | null;
}

/** Fichaje del empleado con sus pausas */
export interface Fichaje {
  id: number;
  fechaEntrada: string;
  fechaSalida: string | null;
  estado: string;
  pausas: Pausa[];
}

@Injectable({
  providedIn: 'root',
})
export class FichajeService {
  private apiUrl = `${environment.apiUrl}/fichajes`;

  constructor(private http: HttpClient) {}

  /** Obtener el fichaje activo del empleado (o 204 si no hay) */
  getFichajeActivo(): Observable<Fichaje> {
    return this.http.get<Fichaje>(`${this.apiUrl}/activo`);
  }

  /** Iniciar un nuevo fichaje */
  iniciarFichaje(): Observable<Fichaje> {
    return this.http.post<Fichaje>(`${this.apiUrl}/iniciar`, {});
  }

  /** Pausar el fichaje activo */
  pausarFichaje(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/pausar`, {});
  }

  /** Reanudar el fichaje (finalizar pausa activa) */
  reanudarFichaje(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/reanudar`, {});
  }

  /** Finalizar el fichaje */
  finalizarFichaje(id: number): Observable<Fichaje> {
    return this.http.put<Fichaje>(`${this.apiUrl}/${id}/finalizar`, {});
  }

  /** Obtener mis fichajes por mes y anio */
  getMisFichajes(mes: number, anio: number): Observable<Fichaje[]> {
    return this.http.get<Fichaje[]>(`${this.apiUrl}/mis-fichajes?mes=${mes}&anio=${anio}`);
  }

  /** Obtener todos los fichajes por mes y anio (admin) */
  getTodosFichajes(mes: number, anio: number): Observable<FichajeAdmin[]> {
    return this.http.get<FichajeAdmin[]>(`${this.apiUrl}/todos?mes=${mes}&anio=${anio}`);
  }
}

/** Fichaje con datos del empleado para vista admin */
export interface FichajeAdmin extends Fichaje {
  empleadoId: number;
  empleadoNombre: string;
}
