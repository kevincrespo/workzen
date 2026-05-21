import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DietaService, DietaAdmin } from '../../../services/dieta-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestionar-dietas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestionar-dietas.html',
  styleUrl: './gestionar-dietas.css',
})
export class GestionarDietas {
  private dietaService = inject(DietaService);

  /** Lista de dietas pendientes */
  dietasPendientes = signal<DietaAdmin[]>([]);
  cargando = signal(true);
  procesando = signal<number | null>(null);

  constructor() {
    this.cargarPendientes();
  }

  /** Carga las dietas pendientes desde el backend */
  cargarPendientes(): void {
    this.cargando.set(true);
    this.dietaService.getTodasDietas().subscribe({
      next: (dietas) => {
        this.dietasPendientes.set(dietas.filter((d) => d.estado === 'pendiente'));
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  /** Aprobar una dieta con confirmacion */
  aprobar(dieta: DietaAdmin): void {
    const nombre = this.nombreCompleto(dieta);
    Swal.fire({
      title: 'Aprobar dieta',
      text: `¿Aprobar la solicitud de dieta de ${nombre}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#48bb78',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Aprobar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(dieta.id);
        this.dietaService.aprobarDieta(dieta.id).subscribe({
          next: () => {
            this.dietasPendientes.update((list) => list.filter((d) => d.id !== dieta.id));
            this.procesando.set(null);
            Swal.fire({
              title: 'Aprobada',
              text: `La dieta de ${nombre} ha sido aprobada.`,
              icon: 'success',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo aprobar la dieta.', 'error');
          },
        });
      }
    });
  }

  /** Rechazar una dieta con confirmacion */
  rechazar(dieta: DietaAdmin): void {
    const nombre = this.nombreCompleto(dieta);
    Swal.fire({
      title: 'Rechazar dieta',
      text: `¿Rechazar la solicitud de dieta de ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Rechazar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(dieta.id);
        this.dietaService.rechazarDieta(dieta.id).subscribe({
          next: () => {
            this.dietasPendientes.update((list) => list.filter((d) => d.id !== dieta.id));
            this.procesando.set(null);
            Swal.fire({
              title: 'Rechazada',
              text: `La dieta de ${nombre} ha sido rechazada.`,
              icon: 'info',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo rechazar la dieta.', 'error');
          },
        });
      }
    });
  }

  /** Nombre completo del empleado */
  nombreCompleto(d: DietaAdmin): string {
    return `${d.empleado.nombre} ${d.empleado.apellido1} ${d.empleado.apellido2 || ''}`.trim();
  }
}
