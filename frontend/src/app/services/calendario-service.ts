import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EventoCalendario {
  empleadoNombre: string;
  empleadoInicial: string;
  tipo: 'ausencia' | 'teletrabajo';
  subtipo: string;
  fechaInicio: string;
  fechaFin: string;
}

export interface CalendarioSemanal {
  semana: { inicio: string; fin: string };
  eventos: EventoCalendario[];
}

@Injectable({
  providedIn: 'root',
})
export class CalendarioService {
  private apiUrl = `${environment.apiUrl}/calendario/semanal`;

  constructor(private http: HttpClient) {}

  /** Obtener eventos de la semana que contiene la fecha dada */
  getSemana(fecha: string): Observable<CalendarioSemanal> {
    return this.http.get<CalendarioSemanal>(`${this.apiUrl}?fecha=${fecha}`);
  }
}
