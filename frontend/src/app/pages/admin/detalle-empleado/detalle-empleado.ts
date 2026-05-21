import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { EmpleadoService } from '../../../services/empleado-service';
import { EmpleadoDetalle, EmpleadoEdicion, Departamento } from '../../../models/empleado';

@Component({
  selector: 'app-detalle-empleado',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './detalle-empleado.html',
  styleUrl: './detalle-empleado.css',
})
export class DetalleEmpleado implements OnInit {
  private empleadoService = inject(EmpleadoService);
  private route = inject(ActivatedRoute);

  /** Datos del empleado */
  empleado = signal<EmpleadoDetalle | null>(null);
  departamentos = signal<Departamento[]>([]);
  cargando = signal(true);
  guardando = signal(false);
  editando = signal(false);

  /** Mensajes */
  mensajeOk = signal('');
  mensajeError = signal('');

  /** Formulario de edicion */
  form = new FormGroup({
    nombre: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    apellido1: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    apellido2: new FormControl('', { nonNullable: true }),
    nif: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    numeroSs: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    direccion: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    provincia: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    localidad: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    codigoPostal: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
    fechaNacimiento: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    iban: new FormControl('', { nonNullable: true }),
    telefono: new FormControl('', { nonNullable: true }),
    departamentoId: new FormControl(0, { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarEmpleado(id);
    this.cargarDepartamentos();
  }

  /** Carga los datos del empleado */
  cargarEmpleado(id: number): void {
    this.cargando.set(true);
    this.empleadoService.getEmpleado(id).subscribe({
      next: (empleado) => {
        this.empleado.set(empleado);
        this.rellenarFormulario(empleado);
        this.cargando.set(false);
      },
      error: () => {
        this.mensajeError.set('No se pudo cargar el empleado');
        this.cargando.set(false);
      },
    });
  }

  /** Carga la lista de departamentos para el select */
  cargarDepartamentos(): void {
    this.empleadoService.getDepartamentos().subscribe({
      next: (deps) => this.departamentos.set(deps),
      error: () => {},
    });
  }

  /** Rellena el formulario con los datos del empleado */
  private rellenarFormulario(e: EmpleadoDetalle): void {
    this.form.patchValue({
      nombre: e.nombre,
      apellido1: e.apellido1,
      apellido2: e.apellido2 || '',
      nif: e.nif,
      numeroSs: e.numeroSs,
      direccion: e.direccion,
      provincia: e.provincia,
      localidad: e.localidad,
      codigoPostal: e.codigoPostal,
      fechaNacimiento: e.fechaNacimiento,
      iban: e.iban || '',
      telefono: e.telefono || '',
      departamentoId: e.departamentoId,
    });
  }

  /** Activa el modo edicion */
  activarEdicion(): void {
    this.editando.set(true);
    this.mensajeOk.set('');
    this.mensajeError.set('');
  }

  /** Cancela la edicion y restaura los datos originales */
  cancelarEdicion(): void {
    const e = this.empleado();
    if (e) this.rellenarFormulario(e);
    this.editando.set(false);
    this.mensajeOk.set('');
    this.mensajeError.set('');
  }

  /** Guarda los cambios del empleado */
  guardar(): void {
    this.mensajeOk.set('');
    this.mensajeError.set('');

    if (this.form.invalid) {
      this.mensajeError.set('Hay campos obligatorios sin rellenar');
      return;
    }

    const empleado = this.empleado();
    if (!empleado) return;

    const datos: EmpleadoEdicion = this.form.getRawValue();

    this.guardando.set(true);
    this.empleadoService.actualizarEmpleado(empleado.id, datos).subscribe({
      next: () => {
        this.guardando.set(false);
        this.editando.set(false);
        this.mensajeOk.set('Datos actualizados correctamente');
        // Recargar para reflejar los cambios
        this.cargarEmpleado(empleado.id);
      },
      error: (err) => {
        this.guardando.set(false);
        if (err.status === 409) {
          this.mensajeError.set('El NIF ya esta en uso por otro empleado');
        } else {
          this.mensajeError.set('Error al guardar los cambios');
        }
      },
    });
  }

  /** Nombre completo para el banner */
  get nombreCompleto(): string {
    const e = this.empleado();
    if (!e) return '';
    return `${e.nombre} ${e.apellido1} ${e.apellido2 || ''}`.trim();
  }
}
