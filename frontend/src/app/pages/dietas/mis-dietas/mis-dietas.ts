import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DietaService, Dieta } from '../../../services/dieta-service';

@Component({
  selector: 'app-mis-dietas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-dietas.html',
  styleUrls: ['./mis-dietas.css'],
})
export class MisDietas {
  private dietaService = inject(DietaService);

  /** Lista reactiva de dietas del empleado */
  dietas = signal<Dieta[]>([]);

  constructor() {
    this.dietaService.getMisDietas().subscribe({
      next: (data) => this.dietas.set(data),
      error: (err) => console.error('Error al cargar mis dietas', err),
    });
  }
}
