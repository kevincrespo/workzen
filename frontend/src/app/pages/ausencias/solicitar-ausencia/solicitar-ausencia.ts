// src/app/pages/ausencias/solicitar-ausencia/solicitar-ausencia.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AusenciaService, SolicitudAusencia } from '../../../services/ausencia-service';

@Component({
  selector: 'app-solicitar-ausencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './solicitar-ausencia.html',
  styleUrls: ['./solicitar-ausencia.css']
})
export class SolicitarAusencia {

  private ausenciaService = inject(AusenciaService);

  // Lista de tipos de ausencia
  tipos = signal<string[]>([]);

  // Mensajes
  mensajeOk   = signal<string>('');
  mensajeError = signal<string>('');
  cargando    = signal<boolean>(false);

  // Formulario reactivo
  form = new FormGroup({
    tipo:        new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    fechaInicio: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    fechaFin:    new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
  });

  constructor() {
    this.cargarTipos();
  }

  private cargarTipos(): void {
    this.ausenciaService.getTiposAusencia().subscribe({
      next: tipos => this.tipos.set(tipos),
      error: () => this.mensajeError.set('No se pudieron cargar los tipos de ausencia')
    });
  }

  onSubmit(): void {
    this.mensajeOk.set('');
    this.mensajeError.set('');

    if (this.form.invalid) {
      this.mensajeError.set('Debes rellenar todos los campos');
      return;
    }

    const { tipo, fechaInicio, fechaFin } = this.form.getRawValue();

    // Ajustar a formato con segundos: yyyy-MM-ddTHH:mm:ss
    const inicioIso = fechaInicio + ':00';
    const finIso    = fechaFin    + ':00';

    // Validación básica en front: inicio <= fin
    if (inicioIso > finIso) {
      this.mensajeError.set('La fecha de inicio no puede ser posterior a la fecha de fin');
      return;
    }

    const solicitud: SolicitudAusencia = {
      tipo: tipo!,
      fechaInicio: inicioIso,
      fechaFin: finIso
    };

    this.cargando.set(true);

    this.ausenciaService.solicitarAusencia(solicitud).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mensajeOk.set('Solicitud enviada correctamente');
        this.form.reset();
        // Dejar select vacío tras reset
        this.form.patchValue({ tipo: '', fechaInicio: '', fechaFin: '' });
      },
      error: (err) => {
        this.cargando.set(false);

        if (err.status === 400) {
          this.mensajeError.set('Fechas inválidas: la fecha de inicio no puede ser posterior a la fecha de fin.');
        } else if (err.status === 409) {
          this.mensajeError.set('La ausencia se solapa con otra ya existente.');
        } else {
          this.mensajeError.set('Error al enviar la solicitud. Inténtalo de nuevo.');
        }
      }
    });
  }
}
