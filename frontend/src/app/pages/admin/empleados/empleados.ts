import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmpleadoService } from '../../../services/empleado-service';
import { EmpleadoResumen } from '../../../models/empleado';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './empleados.html',
  styleUrl: './empleados.css',
})
export class Empleados {
  private empleadoService = inject(EmpleadoService);

  /** Lista de empleados */
  empleados = signal<EmpleadoResumen[]>([]);
  cargando = signal(true);

  constructor() {
    this.cargarEmpleados();
  }

  /** Carga el listado de empleados desde el backend */
  cargarEmpleados(): void {
    this.cargando.set(true);
    this.empleadoService.getEmpleados().subscribe({
      next: (datos) => {
        this.empleados.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  /** Nombre completo del empleado */
  nombreCompleto(e: EmpleadoResumen): string {
    return `${e.nombre} ${e.apellido1} ${e.apellido2 || ''}`.trim();
  }
}
