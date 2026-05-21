import { Component, inject, signal, computed } from '@angular/core';
import { DocumentoService, Documento } from '../../services/documento-service';

@Component({
  selector: 'app-documentacion',
  standalone: true,
  templateUrl: './documentacion.html',
  styleUrl: './documentacion.css',
})
export class Documentacion {
  private documentoService = inject(DocumentoService);

  documentos = signal<Documento[]>([]);
  cargando = signal(true);
  filtroTipo = signal('todos');

  documentosFiltrados = computed(() => {
    const tipo = this.filtroTipo();
    if (tipo === 'todos') return this.documentos();
    return this.documentos().filter(d => d.tipo === tipo);
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

  cambiarFiltro(e: Event): void {
    this.filtroTipo.set((e.target as HTMLSelectElement).value);
  }

  descargar(doc: Documento): void {
    this.documentoService.descargarDocumento(doc.id).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const enlace = document.createElement('a');
      enlace.href = url;
      enlace.download = doc.archivo;
      enlace.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
