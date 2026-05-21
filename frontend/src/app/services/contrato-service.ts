import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Contrato } from '../models/contrato';
import { HttpClient, HttpParams } from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ContratoService {
  private apiUrl = `${environment.apiUrl}/contratos`;
  private http = inject(HttpClient);

  /** Listar contratos (según rol del usuario autenticado) */
  getContratos(): Observable<Contrato[]> {
    return this.http.get<Contrato[]>(this.apiUrl).pipe(
      map((contratos) =>
        contratos.map((c) => ({
          ...c,
          fecha: new Date(c.fecha),
        })),
      ),
    );
  }

  /** Obtener contrato por ID */
  getContratoById(id: number): Observable<Contrato> {
    return this.http.get<Contrato>(`${this.apiUrl}/${id}`).pipe(
      map((c) => ({
        ...c,
        fecha: new Date(c.fecha),
      })),
    );
  }

  /** Buscar por nombre de empleado (ADMIN / RRHH) */
  buscarPorNombre(nombre: string): Observable<Contrato[]> {
    const params = new HttpParams().set('nombre', nombre);
    return this.http
      .get<Contrato[]>(`${this.apiUrl}/buscar-por-nombre`, { params })
      .pipe(map((contratos) => contratos.map((c) => ({ ...c, fecha: new Date(c.fecha) }))));
  }

  /** Buscar por tipo (ADMIN / RRHH) */
  buscarPorTipo(tipo: string): Observable<Contrato[]> {
    const params = new HttpParams().set('tipo', tipo);
    return this.http
      .get<Contrato[]>(`${this.apiUrl}/buscar-por-tipo`, { params })
      .pipe(map((contratos) => contratos.map((c) => ({ ...c, fecha: new Date(c.fecha) }))));
  }

  /** Buscar por rango de fechas (ADMIN / RRHH) */
  buscarPorFechas(fechaDesde: string, fechaHasta: string): Observable<Contrato[]> {
    const params = new HttpParams().set('fechaDesde', fechaDesde).set('fechaHasta', fechaHasta);
    return this.http
      .get<Contrato[]>(`${this.apiUrl}/buscar-por-fechas`, { params })
      .pipe(map((contratos) => contratos.map((c) => ({ ...c, fecha: new Date(c.fecha) }))));
  }

  /** Buscar por departamento (ADMIN / RRHH) */
  buscarPorDepartamento(departamentoId: number): Observable<Contrato[]> {
    const params = new HttpParams().set('departamentoId', departamentoId.toString());
    return this.http
      .get<Contrato[]>(`${this.apiUrl}/buscar-por-departamento`, { params })
      .pipe(map((contratos) => contratos.map((c) => ({ ...c, fecha: new Date(c.fecha) }))));
  }

  /** Descargar contrato (PDF) */
  descargarContrato(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/descargar`, {
      responseType: 'blob',
    });
  }

  /** Subir contrato (ADMIN / RRHH)*/
  subirContrato(datos: FormData): Observable<string> {
    return this.http.post(`${this.apiUrl}/subir`, datos, {
      responseType: 'text',
    });
  }

  /** Eliminar contrato (ADMIN / RRHH)*/
  eliminarContrato(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /** Contar contratos activos (empleado logueado) */
  contarActivos(): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/contar-activos`);
  }
}
