import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JustificanteService, JustificanteDto } from '../../../services/justificante-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestionar-justificantes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestionar-justificantes.html',
  styleUrl: './gestionar-justificantes.css',
})
export class GestionarJustificantes {
  private justificanteService = inject(JustificanteService);

  justificantes = signal<JustificanteDto[]>([]);
  cargando = signal(true);

  constructor() {
    this.cargarJustificantes();
  }

  cargarJustificantes(): void {
    this.cargando.set(true);
    this.justificanteService.getTodos().subscribe({
      next: (datos) => {
        this.justificantes.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  descargar(justificante: JustificanteDto): void {
    this.justificanteService.descargarJustificante(justificante.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = justificante.archivo;
        a.click();
        window.URL.revokeObjectURL(url);
      },
    });
  }

  eliminar(justificante: JustificanteDto): void {
    Swal.fire({
      title: 'Eliminar justificante',
      text: `¿Eliminar el justificante de ${justificante.empleadoNombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.justificanteService.eliminarJustificante(justificante.id).subscribe({
          next: () => {
            this.justificantes.update((list) => list.filter((j) => j.id !== justificante.id));
            Swal.fire({
              title: 'Eliminado',
              text: 'El justificante ha sido eliminado.',
              icon: 'success',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            Swal.fire('Error', 'No se pudo eliminar el justificante.', 'error');
          },
        });
      }
    });
  }
}
