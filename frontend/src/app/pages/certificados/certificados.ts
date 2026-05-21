import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CertificadoService } from '../../services/certificado-service';
import { AuthService } from '../../services/auth';
import { Certificado } from '../../models/certificado';

@Component({
  selector: 'app-certificados',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule],
  templateUrl: './certificados.html',
  styleUrls: ['./certificados.css'],
})
export class Certificados {
  private certificadoService = inject(CertificadoService);
  private authService = inject(AuthService);

  certificados = signal<Certificado[] | null>(null);
  filtroNombre = '';

  get esAdminORecursosHumanos(): boolean {
    return (
      this.authService.tienePrivilegio('admin') ||
      this.authService.tienePrivilegio('rrhh')
    );
  }

  constructor() {
    this.cargarCertificados();
  }

  cargarCertificados(): void {
    this.certificadoService.getCertificados().subscribe({
      next: data => this.certificados.set(data),
      error: err => console.error('Error cargando certificados', err)
    });
  }

  buscarPorNombre(): void {
    if (!this.filtroNombre.trim()) {
      this.cargarCertificados();
      return;
    }
    const todos = this.certificados();
    if (!todos) return;
    const filtrados = todos.filter(c =>
      c.empleadoNombre?.toLowerCase().includes(this.filtroNombre.toLowerCase())
    );
    this.certificados.set(filtrados);
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.cargarCertificados();
  }

  ver(certificado: Certificado): void {
    this.certificadoService.descargarCertificado(certificado.certificadoId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => console.error('Error al visualizar el certificado:', err)
    });
  }

  descargar(certificado: Certificado): void {
    this.certificadoService.descargarCertificado(certificado.certificadoId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = certificado.nombre + '.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error al descargar el certificado:', err)
    });
  }

  eliminar(certificado: Certificado): void {
    if (!confirm(`¿Eliminar el certificado ${certificado.nombre} de ${certificado.empleadoNombre}?`)) return;
    this.certificadoService.eliminarCertificado(certificado.certificadoId).subscribe({
      next: () => this.cargarCertificados(),
      error: (err) => console.error('Error al eliminar el certificado:', err)
    });
  }
}