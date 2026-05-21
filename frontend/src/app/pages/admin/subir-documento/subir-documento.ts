import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { DocumentoService, Documento } from '../../../services/documento-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-subir-documento',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './subir-documento.html',
  styleUrl: './subir-documento.css',
})
export class SubirDocumento {
  private documentoService = inject(DocumentoService);

  documentos = signal<Documento[]>([]);
  cargando = signal(true);
  modalAbierto = signal(false);

  archivoSeleccionado: File | null = null;
  mensaje = '';
  esError = false;
  enviando = false;

  formulario = new FormGroup({
    nombre: new FormControl('', Validators.required),
    tipo: new FormControl('', Validators.required),
  });

  constructor() {
    this.cargarDocumentos();
  }

  cargarDocumentos(): void {
    this.cargando.set(true);
    this.documentoService.getDocumentos().subscribe({
      next: (docs) => {
        this.documentos.set(docs);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }

  abrirModal(): void {
    this.formulario.reset();
    this.archivoSeleccionado = null;
    this.mensaje = '';
    this.esError = false;
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    if (this.enviando) return;
    this.modalAbierto.set(false);
  }

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
    datos.append('nombre', this.formulario.value.nombre!);
    datos.append('tipo', this.formulario.value.tipo!);
    datos.append('archivo', this.archivoSeleccionado);

    this.documentoService.subirDocumento(datos).subscribe({
      next: () => {
        this.enviando = false;
        this.modalAbierto.set(false);
        this.cargarDocumentos();
        Swal.fire({
          title: 'Subido',
          text: 'Documento subido correctamente.',
          icon: 'success',
          confirmButtonColor: '#1353bd',
          timer: 2000,
          showConfirmButton: false,
        });
      },
      error: () => {
        this.mensaje = 'Error al subir el documento. Intentalo de nuevo.';
        this.esError = true;
        this.enviando = false;
      },
    });
  }

  eliminar(doc: Documento): void {
    Swal.fire({
      title: 'Eliminar documento',
      text: `¿Eliminar "${doc.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#a0aec0',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.documentoService.eliminarDocumento(doc.id).subscribe({
          next: () => {
            this.documentos.update(list => list.filter(d => d.id !== doc.id));
            Swal.fire({
              title: 'Eliminado',
              text: `"${doc.nombre}" ha sido eliminado.`,
              icon: 'success',
              confirmButtonColor: '#1353bd',
              timer: 2000,
              showConfirmButton: false,
            });
          },
          error: () => {
            Swal.fire('Error', 'No se pudo eliminar el documento.', 'error');
          },
        });
      }
    });
  }
}
