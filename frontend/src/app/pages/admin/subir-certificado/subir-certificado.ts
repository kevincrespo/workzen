import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AsyncPipe } from '@angular/common';
import { EmpleadoService } from '../../../services/empleado-service';
import { CertificadoService } from '../../../services/certificado-service';

/**
 * Pagina para subir certificados a un empleado.
 * Solo accesible por usuarios con privilegio admin o rrhh.
 */
@Component({
  selector: 'app-subir-certificado',
  standalone: true,
  imports: [ReactiveFormsModule, AsyncPipe],
  templateUrl: './subir-certificado.html',
  styleUrl: './subir-certificado.css',
})
export class SubirCertificado {
  private empleadoService = inject(EmpleadoService);
  private certificadoService = inject(CertificadoService);

  empleados$ = this.empleadoService.getEmpleados();

  archivoSeleccionado: File | null = null;
  mensaje = '';
  esError = false;
  enviando = false;

  formulario = new FormGroup({
    empleadoId: new FormControl<number | null>(null, Validators.required),
    nombre: new FormControl('', Validators.required),
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
    datos.append('nombre', this.formulario.value.nombre!);
    datos.append('fecha', this.formulario.value.fecha!);
    datos.append('archivo', this.archivoSeleccionado);

    this.certificadoService.subirCertificado(datos).subscribe({
      next: () => {
        this.mensaje = 'Certificado subido correctamente.';
        this.esError = false;
        this.enviando = false;
        this.formulario.reset();
        this.archivoSeleccionado = null;
      },
      error: () => {
        this.mensaje = 'Error al subir el certificado. Intentalo de nuevo.';
        this.esError = true;
        this.enviando = false;
      },
    });
  }
}