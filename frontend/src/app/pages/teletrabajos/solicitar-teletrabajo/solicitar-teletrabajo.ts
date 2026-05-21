import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TeletrabajoService, SolicitudTeletrabajo } from '../../../services/teletrabajo-service';

@Component({
  selector: 'app-solicitar-teletrabajo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './solicitar-teletrabajo.html',
  styleUrls: ['./solicitar-teletrabajo.css']
})
export class SolicitarTeletrabajo {
  private teletrabajoService = inject(TeletrabajoService);

  // Mensajes
  mensajeOk = signal<string>('');
  mensajeError = signal<string>('');
  cargando = signal<boolean>(false);

  // Formulario reactivo
  form = new FormGroup({
    fechaInicio: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    fechaFin: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
  });

  onSubmit(): void {
    this.mensajeOk.set('');
    this.mensajeError.set('');

    if (this.form.invalid) {
      this.mensajeError.set('Debes rellenar todos los campos');
      return;
    }

    const { fechaInicio, fechaFin } = this.form.getRawValue();

    // Validación básica en front
    if (fechaInicio > fechaFin) {
      this.mensajeError.set('La fecha de inicio no puede ser posterior a la fecha de fin');
      return;
    }

    const solicitud: SolicitudTeletrabajo = {
      fechaInicio: fechaInicio!,
      fechaFin: fechaFin!,
    };

    this.cargando.set(true);

    this.teletrabajoService.solicitarTeletrabajo(solicitud).subscribe({
      next: () => {
        this.cargando.set(false);
        this.mensajeOk.set('Solicitud de teletrabajo enviada correctamente');
        this.form.reset();
        this.form.patchValue({ fechaInicio: '', fechaFin: '' });
      },
      error: (err) => {
        this.cargando.set(false);

        if (err.status === 400) {
          this.mensajeError.set('No se pudo enviar la solicitud. Revisa las fechas o si se solapa con otra solicitud.');
        } else if (err.status === 401) {
          this.mensajeError.set('Tu sesión ha expirado. Inicia sesión de nuevo.');
        } else if (err.status === 403) {
          this.mensajeError.set('No tienes permisos para realizar esta acción.');
        } else {
          this.mensajeError.set('Error al enviar la solicitud. Inténtalo de nuevo.');
        }
      }
    });
  }
}
