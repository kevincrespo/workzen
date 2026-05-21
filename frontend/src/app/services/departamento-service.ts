import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Departamento } from '../models/departamento';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DepartamentoService {
  private apiUrl = `${environment.apiUrl}/departamentos`;
  private http = inject(HttpClient);

  getDepartamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(this.apiUrl);
  }

  crearDepartamento(nombre: string): Observable<Departamento> {
    return this.http.post<Departamento>(this.apiUrl, { nombre });
  }

  actualizarDepartamento(id: number, nombre: string): Observable<Departamento> {
    return this.http.put<Departamento>(`${this.apiUrl}/${id}`, { nombre });
  }

  eliminarDepartamento(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
