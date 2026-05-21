import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AusenciaService, AusenciaAdmin } from '../../../services/ausencia-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestionar-ausencias',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestionar-ausencias.html',
  styleUrl: './gestionar-ausencias.css',
})
export class GestionarAusencias {
  private ausenciaService = inject(AusenciaService);

  ausenciasPendientes = signal<AusenciaAdmin[]>([]);
  cargando = signal(true);
  procesando = signal<number | null>(null);

  constructor() {
    this.cargarPendientes();
  }

  cargarPendientes(): void {
    this.cargando.set(true);
    this.ausenciaService.getTodasAusencias().subscribe({
      next: (ausencias) => {
        this.ausenciasPendientes.set(
          ausencias.filter(a => a.estado === 'pendiente')
        );
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  aprobar(ausencia: AusenciaAdmin): void {
    const nombre = this.nombreCompleto(ausencia);
    Swal.fire({
      title: 'Aprobar ausencia',
      text: `¿Aprobar la solicitud de ${nombre}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#48bb78',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Aprobar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(ausencia.id);
        this.ausenciaService.aprobarAusencia(ausencia.id).subscribe({
          next: () => {
            this.ausenciasPendientes.update(list => list.filter(a => a.id !== ausencia.id));
            this.procesando.set(null);
            Swal.fire({
              title: 'Aprobada',
              text: `La ausencia de ${nombre} ha sido aprobada.`,
              icon: 'success',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo aprobar la ausencia.', 'error');
          },
        });
      }
    });
  }

  rechazar(ausencia: AusenciaAdmin): void {
    const nombre = this.nombreCompleto(ausencia);
    Swal.fire({
      title: 'Rechazar ausencia',
      text: `¿Rechazar la solicitud de ${nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Rechazar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.procesando.set(ausencia.id);
        this.ausenciaService.rechazarAusencia(ausencia.id).subscribe({
          next: () => {
            this.ausenciasPendientes.update(list => list.filter(a => a.id !== ausencia.id));
            this.procesando.set(null);
            Swal.fire({
              title: 'Rechazada',
              text: `La ausencia de ${nombre} ha sido rechazada.`,
              icon: 'info',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            this.procesando.set(null);
            Swal.fire('Error', 'No se pudo rechazar la ausencia.', 'error');
          },
        });
      }
    });
  }

  nombreCompleto(a: AusenciaAdmin): string {
    return `${a.empleado.nombre} ${a.empleado.apellido1} ${a.empleado.apellido2 || ''}`.trim();
  }
}
