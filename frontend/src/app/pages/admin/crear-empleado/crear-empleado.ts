import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EmpleadoService } from '../../../services/empleado-service';
import { Departamento, EmpleadoCreacion } from '../../../models/empleado';

@Component({
  selector: 'app-crear-empleado',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './crear-empleado.html',
  styleUrl: './crear-empleado.css',
})
export class CrearEmpleado {
  private empleadoService = inject(EmpleadoService);
  private router = inject(Router);

  /** Departamentos para el select */
  departamentos = signal<Departamento[]>([]);

  /** Privilegios disponibles */
  privilegiosDisponibles = ['admin', 'rrhh', 'empleado'];

  /** Estado del formulario */
  guardando = signal(false);
  mensajeOk = signal('');
  mensajeError = signal('');

  /** Formulario reactivo */
  form = new FormGroup({
    // Cuenta de usuario
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    privilegios: new FormControl<string[]>([], { nonNullable: true }),

    // Datos personales
    nombre: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    apellido1: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    apellido2: new FormControl('', { nonNullable: true }),
    nif: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    numeroSs: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    fechaNacimiento: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    telefono: new FormControl('', { nonNullable: true }),

    // Direccion
    direccion: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    provincia: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    localidad: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    codigoPostal: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),

    // Datos laborales
    departamentoId: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
    iban: new FormControl('', { nonNullable: true }),
  });

  constructor() {
    this.cargarDepartamentos();
  }

  /** Carga los departamentos para el select */
  private cargarDepartamentos(): void {
    this.empleadoService.getDepartamentos().subscribe({
      next: (deps) => this.departamentos.set(deps),
    });
  }

  /** Alterna un privilegio en la lista de seleccionados */
  togglePrivilegio(privilegio: string): void {
    const actuales = this.form.controls.privilegios.value;
    if (actuales.includes(privilegio)) {
      this.form.controls.privilegios.setValue(actuales.filter((p) => p !== privilegio));
    } else {
      this.form.controls.privilegios.setValue([...actuales, privilegio]);
    }
  }

  /** Comprueba si un privilegio esta seleccionado */
  tienePrivilegio(privilegio: string): boolean {
    return this.form.controls.privilegios.value.includes(privilegio);
  }

  /** Envia el formulario al backend */
  guardar(): void {
    this.mensajeOk.set('');
    this.mensajeError.set('');

    if (this.form.invalid) {
      this.mensajeError.set('Hay campos obligatorios sin rellenar');
      return;
    }

    const datos: EmpleadoCreacion = this.form.getRawValue();
    this.guardando.set(true);

    this.empleadoService.crearEmpleado(datos).subscribe({
      next: () => {
        this.guardando.set(false);
        this.mensajeOk.set('Empleado creado correctamente');
        setTimeout(() => this.router.navigate(['/empleados']), 1500);
      },
      error: (err) => {
        this.guardando.set(false);
        if (err.status === 409) {
          this.mensajeError.set('El email o NIF ya esta en uso');
        } else {
          this.mensajeError.set('Error al crear el empleado');
        }
      },
    });
  }
}
