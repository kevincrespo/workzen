import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeletrabajoService, TeletrabajoAdmin } from '../../../services/teletrabajo-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestionar-teletrabajos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestionar-teletrabajos.html',
  styleUrl: './gestionar-teletrabajos.css',
})
export class GestionarTeletrabajos {
  private teletrabajoService = inject(TeletrabajoService);

  teletrabajosPendientes = signal<TeletrabajoAdmin[]>([]);
  cargando = signal(true);
  procesando = signal<number | null>(null);

  constructor() {
    this.cargarPendientes();
  }

  cargarPendientes(): void {
    this.cargando.set(true);

    this.teletrabajoService.getTeletrabajosPendientes().subscribe({
      next: (teletrabajos) => {
        // El backend ya devuelve pendientes, pero dejamos filtro por seguridad
        this.teletrabajosPendientes.set(
          teletrabajos.filter(t => t.estado === 'pendiente')
        );
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  aprobar(teletrabajo: TeletrabajoAdmin): void {
    const nombre = this.nombreCompleto(teletrabajo);

    Swal.fire({
      title: 'Aprobar teletrabajo',
      text: `¿Aprobar la solicitud de ${nombre}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#48bb78',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Aprobar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(teletrabajo.id);

        this.teletrabajoService.aprobarTeletrabajo(teletrabajo.id).subscribe({
          next: () => {
            this.teletrabajosPendientes.update(list =>
              list.filter(t => t.id !== teletrabajo.id)
            );
            this.procesando.set(null);

            Swal.fire({
              title: 'Aprobado',
              text: `El teletrabajo de ${nombre} ha sido aprobado.`,
              icon: 'success',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo aprobar el teletrabajo.', 'error');
          },
        });
      }
    });
  }

  rechazar(teletrabajo: TeletrabajoAdmin): void {
    const nombre = this.nombreCompleto(teletrabajo);

    Swal.fire({
      title: 'Rechazar teletrabajo',
      text: `¿Rechazar la solicitud de ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Rechazar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(teletrabajo.id);

        this.teletrabajoService.rechazarTeletrabajo(teletrabajo.id).subscribe({
          next: () => {
            this.teletrabajosPendientes.update(list =>
              list.filter(t => t.id !== teletrabajo.id)
            );
            this.procesando.set(null);

            Swal.fire({
              title: 'Rechazado',
              text: `El teletrabajo de ${nombre} ha sido rechazado.`,
              icon: 'info',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo rechazar el teletrabajo.', 'error');
          },
        });
      }
    });
  }

  nombreCompleto(t: TeletrabajoAdmin): string {
    return `${t.empleado.nombre} ${t.empleado.apellido1} ${t.empleado.apellido2 || ''}`.trim();
  }
}
