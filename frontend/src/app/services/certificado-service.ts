import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Certificado } from '../models/certificado';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CertificadoService {

  private apiUrl = `${environment.apiUrl}/certificados`;
  private http = inject(HttpClient);

  getCertificados(): Observable<Certificado[]> {
    return this.http.get<Certificado[]>(this.apiUrl);
  }

  getCertificadosPorEmpleado(empleadoId: number): Observable<Certificado[]> {
    return this.http.get<Certificado[]>(`${this.apiUrl}/empleado/${empleadoId}`);
  }

  subirCertificado(datos: FormData): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/subir`, datos);
  }

  descargarCertificado(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/descargar`, {
      responseType: 'blob',
    });
  }

  eliminarCertificado(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}