import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Festivo } from '../models/festivo';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FestivoService {
  private apiUrl = `${environment.apiUrl}/festivos`;
  private http = inject(HttpClient);

  getFestivos(): Observable<Festivo[]> {
    return this.http.get<Festivo[]>(this.apiUrl);
  }

  crearFestivo(nombre: string, fecha: string): Observable<Festivo> {
    return this.http.post<Festivo>(this.apiUrl, { nombre, fecha });
  }

  actualizarFestivo(id: number, nombre: string, fecha: string): Observable<Festivo> {
    return this.http.put<Festivo>(`${this.apiUrl}/${id}`, { nombre, fecha });
  }

  eliminarFestivo(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
