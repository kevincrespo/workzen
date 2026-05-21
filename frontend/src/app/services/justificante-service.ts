import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface JustificanteDto {
  id: number;
  motivo: string;
  archivo: string;
  ausenciaId: number;
  fechaInicio: string;
  fechaFin: string;
  empleadoNombre?: string;
}

export interface AusenciaPendiente {
  id: number;
  tipo: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}

@Injectable({
  providedIn: 'root',
})
export class JustificanteService {
  private apiUrl = `${environment.apiUrl}/justificantes`;
  private http = inject(HttpClient);

  /** Obtiene las ausencias medicas aprobadas sin justificante del empleado */
  getAusenciasPendientes(): Observable<AusenciaPendiente[]> {
    return this.http.get<AusenciaPendiente[]>(`${this.apiUrl}/ausencias-pendientes`);
  }

  /** Obtiene los justificantes del empleado autenticado */
  getMisJustificantes(): Observable<JustificanteDto[]> {
    return this.http.get<JustificanteDto[]>(`${this.apiUrl}/mis-justificantes`);
  }

  /** Sube un justificante para una ausencia */
  subirJustificante(ausenciaId: number, archivo: File, motivo: string): Observable<string> {
    const formData = new FormData();
    formData.append('ausenciaId', ausenciaId.toString());
    formData.append('archivo', archivo);
    if (motivo) {
      formData.append('motivo', motivo);
    }
    return this.http.post(`${this.apiUrl}/subir`, formData, { responseType: 'text' });
  }

  /** Descarga un justificante */
  descargarJustificante(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/descargar`, { responseType: 'blob' });
  }

  /** Obtiene todos los justificantes (admin) */
  getTodos(): Observable<JustificanteDto[]> {
    return this.http.get<JustificanteDto[]>(this.apiUrl);
  }

  /** Elimina un justificante (admin) */
  eliminarJustificante(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
