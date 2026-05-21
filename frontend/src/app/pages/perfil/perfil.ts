import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { EmpleadoService } from '../../services/empleado-service';
import { EmpleadoDetalle } from '../../models/empleado';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private authService = inject(AuthService);
  private empleadoService = inject(EmpleadoService);

  /** Datos del perfil */
  perfil = signal<EmpleadoDetalle | null>(null);
  cargando = signal(true);

  get email(): string {
    return this.authService.getEmail();
  }

  /** Nombre completo del empleado */
  get nombreCompleto(): string {
    const p = this.perfil();
    if (!p) return this.authService.getNombre();
    return `${p.nombre} ${p.apellido1} ${p.apellido2 || ''}`.trim();
  }

  /** Inicial para el avatar */
  get inicial(): string {
    const p = this.perfil();
    return (p?.nombre ?? this.authService.getNombre()).charAt(0).toUpperCase();
  }

  constructor() {
    this.cargarPerfil();
  }

  /** Carga el perfil del empleado autenticado */
  cargarPerfil(): void {
    this.cargando.set(true);
    this.empleadoService.getMiPerfil().subscribe({
      next: (datos) => {
        this.perfil.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
      },
    });
  }
}
