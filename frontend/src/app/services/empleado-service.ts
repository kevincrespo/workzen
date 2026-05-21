import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Empleado,
  EmpleadoResumen,
  EmpleadoDetalle,
  EmpleadoEdicion,
  EmpleadoCreacion,
  Departamento,
} from '../models/empleado';
import { environment } from '../../environments/environment';

/** Respuesta del endpoint /empleados/dias-disponibles */
export interface DiasDisponibles {
  diasTotales: number;
  diasConsumidos: number;
  diasDisponibles: number;
}

@Injectable({
  providedIn: 'root',
})
export class EmpleadoService {
  private apiUrl = `${environment.apiUrl}/empleados`;
  private departamentosUrl = `${environment.apiUrl}/departamentos`;

  constructor(private http: HttpClient) {}

  /** Obtener listado basico de empleados (id, nombre, apellidos) */
  getEmpleadosBasico(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(this.apiUrl);
  }

  /** Obtener listado resumido con departamento (admin/rrhh) */
  getEmpleados(): Observable<EmpleadoResumen[]> {
    return this.http.get<EmpleadoResumen[]>(this.apiUrl);
  }

  /** Obtener detalle completo de un empleado */
  getEmpleado(id: number): Observable<EmpleadoDetalle> {
    return this.http.get<EmpleadoDetalle>(`${this.apiUrl}/${id}`);
  }

  /** Actualizar datos de un empleado */
  actualizarEmpleado(id: number, datos: EmpleadoEdicion): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, datos);
  }

  /** Obtener perfil del empleado autenticado */
  getMiPerfil(): Observable<EmpleadoDetalle> {
    return this.http.get<EmpleadoDetalle>(`${environment.apiUrl}/perfil`);
  }

  /** Obtener lista de departamentos */
  getDepartamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(this.departamentosUrl);
  }

  /** Crear un nuevo empleado con su cuenta de usuario */
  crearEmpleado(datos: EmpleadoCreacion): Observable<void> {
    return this.http.post<void>(this.apiUrl, datos);
  }

  /** Obtener los dias de vacaciones disponibles del empleado autenticado */
  getDiasDisponibles(): Observable<DiasDisponibles> {
    return this.http.get<DiasDisponibles>(`${this.apiUrl}/dias-disponibles`);
  }
}
