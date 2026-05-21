import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeletrabajoService, Teletrabajo } from '../../../services/teletrabajo-service';

@Component({
  selector: 'app-mis-teletrabajos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-teletrabajos.html',
  styleUrls: ['./mis-teletrabajos.css']
})
export class MisTeletrabajos {
  private teletrabajoService = inject(TeletrabajoService);

  teletrabajos = signal<Teletrabajo[]>([]);
  cargando = signal<boolean>(true);
  error = signal<string>('');

  constructor() {
    this.cargarMisTeletrabajos();
  }

  cargarMisTeletrabajos(): void {
    this.cargando.set(true);
    this.error.set('');

    this.teletrabajoService.getMisTeletrabajos().subscribe({
      next: (data) => {
        console.log('Teletrabajos recibidos:', data);
        this.teletrabajos.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar mis teletrabajos', err);
        this.error.set('No se pudieron cargar tus teletrabajos');
        this.cargando.set(false);
      }
    });
  }
}
