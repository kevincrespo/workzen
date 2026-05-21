import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { FestivoService } from '../../../services/festivo-service';
import { Festivo } from '../../../models/festivo';

@Component({
  selector: 'app-festivos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './festivos.html',
  styleUrl: './festivos.css',
})
export class Festivos {
  private festivoService = inject(FestivoService);

  festivos = signal<Festivo[]>([]);
  cargando = signal(true);
  editandoId = signal<number | null>(null);
  editandoNombre = signal('');
  editandoFecha = signal('');
  mensaje = signal('');
  tipoMensaje = signal<'exito' | 'error' | ''>('');

  form = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(2)]),
    fecha: new FormControl('', [Validators.required]),
  });

  constructor() {
    this.cargarFestivos();
  }

  cargarFestivos(): void {
    this.cargando.set(true);
    this.festivoService.getFestivos().subscribe({
      next: (datos) => {
        this.festivos.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.mostrarMensaje('Error al cargar los festivos', 'error');
      },
    });
  }

  crearFestivo(): void {
    const nombre = this.form.value.nombre?.trim();
    const fecha = this.form.value.fecha;
    if (!nombre || !fecha) return;

    this.festivoService.crearFestivo(nombre, fecha).subscribe({
      next: (festivo) => {
        this.festivos.update((lista) => [...lista, festivo]);
        this.form.reset();
        this.mostrarMensaje('Festivo creado correctamente', 'exito');
      },
      error: () => {
        this.mostrarMensaje('Error al crear el festivo', 'error');
      },
    });
  }

  iniciarEdicion(f: Festivo): void {
    this.editandoId.set(f.id);
    this.editandoNombre.set(f.nombre);
    this.editandoFecha.set(f.fecha);
  }

  cancelarEdicion(): void {
    this.editandoId.set(null);
    this.editandoNombre.set('');
    this.editandoFecha.set('');
  }

  guardarEdicion(id: number): void {
    const nombre = this.editandoNombre().trim();
    const fecha = this.editandoFecha();
    if (!nombre || !fecha) return;

    this.festivoService.actualizarFestivo(id, nombre, fecha).subscribe({
      next: (actualizado) => {
        this.festivos.update((lista) =>
          lista.map((f) => (f.id === id ? actualizado : f)),
        );
        this.cancelarEdicion();
        this.mostrarMensaje('Festivo actualizado correctamente', 'exito');
      },
      error: () => {
        this.mostrarMensaje('Error al actualizar el festivo', 'error');
      },
    });
  }

  eliminarFestivo(f: Festivo): void {
    Swal.fire({
      title: 'Eliminar festivo',
      text: `¿Eliminar "${f.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.festivoService.eliminarFestivo(f.id).subscribe({
          next: () => {
            this.festivos.update((lista) => lista.filter((d) => d.id !== f.id));
            Swal.fire({
              title: 'Eliminado',
              text: `"${f.nombre}" ha sido eliminado.`,
              icon: 'success',
              confirmButtonColor: '#14b8a6',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            Swal.fire('Error', 'No se pudo eliminar el festivo.', 'error');
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
