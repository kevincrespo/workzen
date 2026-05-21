import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { DepartamentoService } from '../../../services/departamento-service';
import { Departamento } from '../../../models/departamento';

@Component({
  selector: 'app-departamentos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './departamentos.html',
  styleUrl: './departamentos.css',
})
export class Departamentos {
  private departamentoService = inject(DepartamentoService);

  departamentos = signal<Departamento[]>([]);
  cargando = signal(true);
  editandoId = signal<number | null>(null);
  editandoNombre = signal('');
  mensaje = signal('');
  tipoMensaje = signal<'exito' | 'error' | ''>('');

  nuevoNombre = new FormControl('', [Validators.required, Validators.minLength(2)]);

  constructor() {
    this.cargarDepartamentos();
  }

  cargarDepartamentos(): void {
    this.cargando.set(true);
    this.departamentoService.getDepartamentos().subscribe({
      next: (datos) => {
        this.departamentos.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mostrarMensaje('Error al cargar los departamentos', 'error');
      },
    });
  }

  crearDepartamento(): void {
    const nombre = this.nuevoNombre.value?.trim();
    if (!nombre) return;

    this.departamentoService.crearDepartamento(nombre).subscribe({
      next: (dep) => {
        this.departamentos.update((lista) => [...lista, dep]);
        this.nuevoNombre.reset();
        this.mostrarMensaje('Departamento creado correctamente', 'exito');
      },
      error: () => {
        this.mostrarMensaje('Error al crear el departamento', 'error');
      },
    });
  }

  iniciarEdicion(dep: Departamento): void {
    this.editandoId.set(dep.id);
    this.editandoNombre.set(dep.nombre);
  }

  cancelarEdicion(): void {
    this.editandoId.set(null);
    this.editandoNombre.set('');
  }

  guardarEdicion(id: number): void {
    const nombre = this.editandoNombre().trim();
    if (!nombre) return;

    this.departamentoService.actualizarDepartamento(id, nombre).subscribe({
      next: (actualizado) => {
        this.departamentos.update((lista) =>
          lista.map((d) => (d.id === id ? actualizado : d)),
        );
        this.cancelarEdicion();
        this.mostrarMensaje('Departamento actualizado correctamente', 'exito');
      },
      error: () => {
        this.mostrarMensaje('Error al actualizar el departamento', 'error');
      },
    });
  }

  eliminarDepartamento(dep: Departamento): void {
    Swal.fire({
      title: 'Eliminar departamento',
      text: `¿Eliminar "${dep.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.departamentoService.eliminarDepartamento(dep.id).subscribe({
          next: () => {
            this.departamentos.update((lista) => lista.filter((d) => d.id !== dep.id));
            Swal.fire({
              title: 'Eliminado',
              text: `"${dep.nombre}" ha sido eliminado.`,
              icon: 'success',
              confirmButtonColor: '#14b8a6',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: (err) => {
            if (err.status === 409) {
              Swal.fire('Error', err.error || 'No se puede eliminar: tiene empleados asignados', 'error');
            } else {
              Swal.fire('Error', 'No se pudo eliminar el departamento.', 'error');
            }
          },
        });
      }
    });
  }

  private mostrarMensaje(texto: string, tipo: 'exito' | 'error'): void {
    this.mensaje.set(texto);
    this.tipoMensaje.set(tipo);
    setTimeout(() => {
      this.mensaje.set('');
      this.tipoMensaje.set('');
    }, 4000);
  }
}
