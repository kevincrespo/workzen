import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Nomina } from '../models/nomina';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class NominaService {
  private apiUrl = `${environment.apiUrl}/nominas`;
  private http = inject(HttpClient);

  getNominas(year: number): Observable<Nomina[]> {
    const params = new HttpParams().set('year', year.toString());
    return this.http.get<Nomina[]>(this.apiUrl, { params });
  }

  contarNominas(): Observable<number> {
    return this.http.get<number>(this.apiUrl + '/contar');
  }

  /** Descargar el archivo de una nomina por su ID */
  descargarNomina(id: number): Observable<Blob> {
    return this.http.get(this.apiUrl + '/' + id + '/descargar', {
      responseType: 'blob',
    });
  }

  /** Subir una nomina al servidor (multipart/form-data) */
  subirNomina(datos: FormData): Observable<string> {
    return this.http.post(this.apiUrl + '/subir', datos, {
      responseType: 'text',
    });
  }
}
