import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AusenciaService, Ausencia } from '../../../services/ausencia-service';

@Component({
  selector: 'app-mis-ausencias',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-ausencias.html',
  styleUrls: ['./mis-ausencias.css']
})
export class MisAusencias {
  private ausenciaService = inject(AusenciaService);

  // Lista reactiva de ausencias
  ausencias = signal<Ausencia[]>([]);

  constructor() {
    this.ausenciaService.getMisAusencias().subscribe({
      next: data => {
        console.log('Ausencias recibidas:', data);
        this.ausencias.set(data);
      },
      error: err => {
        console.error('Error al cargar mis ausencias', err);
      }
    });
  }
}
