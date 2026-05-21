import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface Documento {
  id: number;
  nombre: string;
  tipo: string;
  archivo: string;
}

@Injectable({
  providedIn: 'root',
})
export class DocumentoService {
  private apiUrl = `${environment.apiUrl}/documentos`;
  private http = inject(HttpClient);

  getDocumentos(): Observable<Documento[]> {
    return this.http.get<Documento[]>(this.apiUrl);
  }

  subirDocumento(datos: FormData): Observable<string> {
    return this.http.post(this.apiUrl + '/subir', datos, {
      responseType: 'text',
    });
  }

  descargarDocumento(id: number): Observable<Blob> {
    return this.http.get(this.apiUrl + '/' + id + '/descargar', {
      responseType: 'blob',
    });
  }

  eliminarDocumento(id: number): Observable<void> {
    return this.http.delete<void>(this.apiUrl + '/' + id);
  }
}
