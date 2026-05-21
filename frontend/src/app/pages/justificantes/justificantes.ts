import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  JustificanteService,
  JustificanteDto,
  AusenciaPendiente,
} from '../../services/justificante-service';

@Component({
  selector: 'app-justificantes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './justificantes.html',
  styleUrl: './justificantes.css',
})
export class Justificantes {
  private justificanteService = inject(JustificanteService);

  ausenciasPendientes = signal<AusenciaPendiente[]>([]);
  misJustificantes = signal<JustificanteDto[]>([]);
  cargando = signal(true);

  /** Estado del formulario de subida */
  subiendoId = signal<number | null>(null);
  archivoSeleccionado: File | null = null;
  motivo = '';
  procesando = signal(false);
  mensaje = signal('');
  tipoMensaje = signal<'success' | 'danger'>('success');

  constructor() {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando.set(true);

    this.justificanteService.getAusenciasPendientes().subscribe({
      next: (ausencias) => {
        this.ausenciasPendientes.set(ausencias);
        this.cargarJustificantes();
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  private cargarJustificantes(): void {
    this.justificanteService.getMisJustificantes().subscribe({
      next: (justificantes) => {
        this.misJustificantes.set(justificantes);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  /** Muestra el formulario de subida para una ausencia */
  mostrarFormulario(ausenciaId: number): void {
    this.subiendoId.set(ausenciaId);
    this.archivoSeleccionado = null;
    this.motivo = '';
    this.mensaje.set('');
  }

  /** Cancela la subida */
  cancelarSubida(): void {
    this.subiendoId.set(null);
    this.archivoSeleccionado = null;
    this.motivo = '';
  }

  /** Maneja la seleccion de archivo */
  onArchivoSeleccionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.archivoSeleccionado = input.files[0];
    }
  }

  /** Sube el justificante */
  subirJustificante(): void {
    const ausenciaId = this.subiendoId();
    if (!ausenciaId || !this.archivoSeleccionado) return;

    this.procesando.set(true);
    this.justificanteService
      .subirJustificante(ausenciaId, this.archivoSeleccionado, this.motivo)
      .subscribe({
        next: () => {
          this.mensaje.set('Justificante subido correctamente');
          this.tipoMensaje.set('success');
          this.subiendoId.set(null);
          this.archivoSeleccionado = null;
          this.motivo = '';
          this.procesando.set(false);
          this.cargarDatos();
        },
        error: () => {
          this.mensaje.set('Error al subir el justificante');
          this.tipoMensaje.set('danger');
          this.procesando.set(false);
        },
      });
  }

  /** Descarga un justificante */
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
}
