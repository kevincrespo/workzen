import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { DietaService, SolicitudDieta } from '../../../services/dieta-service';

@Component({
  selector: 'app-solicitar-dieta',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './solicitar-dieta.html',
  styleUrls: ['./solicitar-dieta.css'],
})
export class SolicitarDieta {
  private dietaService = inject(DietaService);

  /** Tipos de dieta disponibles (fijos) */
  tipos = ['nacional', 'internacional'];

  /** Mensajes de estado */
  mensajeOk = signal<string>('');
  mensajeError = signal<string>('');
  cargando = signal<boolean>(false);

  /** Formulario reactivo */
  form = new FormGroup({
    tipo: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    fechaInicio: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    fechaFin: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    motivo: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  /** Enviar solicitud de dieta */
  onSubmit(): void {
    this.mensajeOk.set('');
    this.mensajeError.set('');

    if (this.form.invalid) {
      this.mensajeError.set('Debes rellenar todos los campos');
      return;
    }

    const { tipo, fechaInicio, fechaFin, motivo } = this.form.getRawValue();

    // Validacion: inicio <= fin
    if (fechaInicio > fechaFin) {
      this.mensajeError.set('La fecha de inicio no puede ser posterior a la fecha de fin');
      return;
    }

    const solicitud: SolicitudDieta = {
      tipo,
      fechaInicio,
      fechaFin,
      motivo,
    };

    this.cargando.set(true);

    this.dietaService.solicitarDieta(solicitud).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mensajeOk.set('Solicitud de dieta enviada correctamente');
        this.form.reset();
        this.form.patchValue({ tipo: '', fechaInicio: '', fechaFin: '', motivo: '' });
      },
      error: (err) => {
        this.cargando.set(false);

        if (err.status === 400) {
          this.mensajeError.set('Datos invalidos. Revisa los campos e intentalo de nuevo.');
        } else if (err.status === 409) {
          this.mensajeError.set('La dieta se solapa con otra ya existente.');
        } else {
          this.mensajeError.set('Error al enviar la solicitud. Intentalo de nuevo.');
        }
      },
    });
  }
}
