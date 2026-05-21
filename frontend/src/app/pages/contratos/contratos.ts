import { Component, inject, signal } from '@angular/core';
import { ContratoService } from '../../services/contrato-service';
import { AuthService } from '../../services/auth';
import { DepartamentoService } from '../../services/departamento-service';
import { Contrato } from '../../models/contrato';
import { Departamento } from '../../models/departamento';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contratos',
  standalone: true,
  templateUrl: './contratos.html',
  styleUrl: './contratos.css',
  imports: [DatePipe, FormsModule],
})
export class Contratos {
  private contratoService = inject(ContratoService);
  private authService = inject(AuthService);
  private departamentoService = inject(DepartamentoService);

  contratos = signal<Contrato[] | null>(null);
  departamentos = signal<Departamento[]>([]);

  // Filtros
  filtroNombre = '';
  filtroTipo = '';
  filtroFechaDesde = '';
  filtroFechaHasta = '';
  filtroDepartamentoId: number | null = null;

  tiposContrato = [
    'alta',
    'baja',
    'modificacion',
  ];

  get esAdminORecursosHumanos(): boolean {
    return (
      this.authService.tienePrivilegio('admin') ||
      this.authService.tienePrivilegio('rrhh')
    );
  }

  constructor() {
    this.cargarContratos();
    if (this.esAdminORecursosHumanos) {
      this.departamentoService.getDepartamentos().subscribe({
        next: data => this.departamentos.set(data),
        error: err => console.error('Error cargando departamentos:', err)
      });
    }
  }

  cargarContratos(): void {
    this.contratoService.getContratos().subscribe({
      next: data => this.contratos.set(data),
      error: err => console.error('Error cargando contratos:', err)
    });
  }

  buscarPorNombre(): void {
    if (!this.filtroNombre.trim()) {
      this.cargarContratos();
      return;
    }
    this.contratoService.buscarPorNombre(this.filtroNombre).subscribe({
      next: data => this.contratos.set(data),
      error: err => console.error('Error buscando por nombre:', err)
    });
  }

  buscarPorTipo(): void {
    if (!this.filtroTipo) {
      this.cargarContratos();
      return;
    }
    this.contratoService.buscarPorTipo(this.filtroTipo).subscribe({
      next: data => this.contratos.set(data),
      error: err => console.error('Error buscando por tipo:', err)
    });
  }

  buscarPorFechas(): void {
    if (!this.filtroFechaDesde || !this.filtroFechaHasta) return;
    this.contratoService.buscarPorFechas(this.filtroFechaDesde, this.filtroFechaHasta).subscribe({
      next: data => this.contratos.set(data),
      error: err => console.error('Error buscando por fechas:', err)
    });
  }

  buscarPorDepartamento(): void {
    if (!this.filtroDepartamentoId) {
      this.cargarContratos();
      return;
    }
    this.contratoService.buscarPorDepartamento(this.filtroDepartamentoId).subscribe({
      next: data => this.contratos.set(data),
      error: err => console.error('Error buscando por departamento:', err)
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroTipo = '';
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.filtroDepartamentoId = null;
    this.cargarContratos();
  }

  ver(contrato: Contrato): void {
    this.contratoService.descargarContrato(contrato.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => console.error('Error al visualizar el contrato:', err)
    });
  }

  descargar(contrato: Contrato): void {
    this.contratoService.descargarContrato(contrato.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = contrato.archivo ?? `contrato-${contrato.id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error al descargar el contrato:', err)
    });
  }

  eliminar(contrato: Contrato): void {
    if (!confirm(`¿Eliminar el contrato de ${contrato.empleado?.nombre}?`)) return;
    this.contratoService.eliminarContrato(contrato.id).subscribe({
      next: () => this.cargarContratos(),
      error: (err) => console.error('Error al eliminar el contrato:', err)
    });
  }
}