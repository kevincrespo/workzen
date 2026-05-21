import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AsyncPipe } from '@angular/common';
import { EmpleadoService } from '../../../services/empleado-service';
import { ContratoService } from '../../../services/contrato-service';

/**
 * Pagina para subir contratos a un empleado.
 * Solo accesible por usuarios con privilegio admin o rrhh.
 */
@Component({
  selector: 'app-subir-contrato',
  standalone: true,
  imports: [ReactiveFormsModule, AsyncPipe],
  templateUrl: './subir-contrato.html',
  styleUrl: './subir-contrato.css',
})
export class SubirContrato {
  private empleadoService = inject(EmpleadoService);
  private contratoService = inject(ContratoService);

  empleados$ = this.empleadoService.getEmpleados();

  archivoSeleccionado: File | null = null;
  mensaje = '';
  esError = false;
  enviando = false;

  tiposContrato = [
    'alta',
    'baja',
    'modificacion',
  ];

  formulario = new FormGroup({
    empleadoId: new FormControl<number | null>(null, Validators.required),
    tipo: new FormControl('', Validators.required),
    salario: new FormControl<number | null>(null),
    fecha: new FormControl('', Validators.required),
  });

  onArchivoSeleccionado(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    this.archivoSeleccionado = input.files?.[0] ?? null;
  }

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
    datos.append('tipo', this.formulario.value.tipo!);
    datos.append('fecha', this.formulario.value.fecha!);
    datos.append('archivo', this.archivoSeleccionado);

    if (this.formulario.value.salario) {
      datos.append('salario', String(this.formulario.value.salario));
    }

    this.contratoService.subirContrato(datos).subscribe({
      next: () => {
        this.mensaje = 'Contrato subido correctamente.';
        this.esError = false;
        this.enviando = false;
        this.formulario.reset();
        this.archivoSeleccionado = null;
      },
      error: () => {
        this.mensaje = 'Error al subir el contrato. Intentalo de nuevo.';
        this.esError = true;
        this.enviando = false;
      },
    });
  }
}