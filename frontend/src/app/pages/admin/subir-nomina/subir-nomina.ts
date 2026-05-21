import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AsyncPipe } from '@angular/common';
import { EmpleadoService } from '../../../services/empleado-service';
import { NominaService } from '../../../services/nomina-service';

/**
 * Pagina para subir nominas a un empleado.
 * Solo accesible por usuarios con privilegio admin o rrhh.
 * Muestra un formulario con: select de empleado, fecha y archivo.
 */
@Component({
  selector: 'app-subir-nomina',
  standalone: true,
  imports: [ReactiveFormsModule, AsyncPipe],
  templateUrl: './subir-nomina.html',
  styleUrl: './subir-nomina.css',
})
export class SubirNomina {
  private empleadoService = inject(EmpleadoService);
  private nominaService = inject(NominaService);

  /** Lista de empleados para el select */
  empleados$ = this.empleadoService.getEmpleados();

  /** Archivo seleccionado por el usuario */
  archivoSeleccionado: File | null = null;

  /** Mensajes de estado */
  mensaje = '';
  esError = false;
  enviando = false;

  /** Formulario reactivo */
  formulario = new FormGroup({
    empleadoId: new FormControl<number | null>(null, Validators.required),
    fecha: new FormControl('', Validators.required),
  });

  /** Capturar el archivo del input */
  onArchivoSeleccionado(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    this.archivoSeleccionado = input.files?.[0] ?? null;
  }

  /** Enviar el formulario al backend */
  onSubmit(): void {
    if (this.formulario.invalid || !this.archivoSeleccionado) {
      this.mensaje = 'Completa todos los campos y selecciona un archivo.';
      this.esError = true;
      return;
    }

    this.enviando = true;
    this.mensaje = '';

    const datos = new FormData();
    datos.append('empleadoId', String(this.formulario.value.empleadoId));
    datos.append('fecha', this.formulario.value.fecha!);
    datos.append('archivo', this.archivoSeleccionado);

    this.nominaService.subirNomina(datos).subscribe({
      next: () => {
        this.mensaje = 'Nomina subida correctamente.';
        this.esError = false;
        this.enviando = false;
        this.formulario.reset();
        this.archivoSeleccionado = null;
      },
      error: () => {
        this.mensaje = 'Error al subir la nomina. Intentalo de nuevo.';
        this.esError = true;
        this.enviando = false;
      },
    });
  }
}
