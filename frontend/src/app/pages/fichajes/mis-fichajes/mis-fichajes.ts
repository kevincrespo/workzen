import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FichajeService, Fichaje, Pausa } from '../../../services/fichaje-service';

@Component({
  selector: 'app-mis-fichajes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-fichajes.html',
  styleUrls: ['./mis-fichajes.css'],
})
export class MisFichajes {
  private fichajeService = inject(FichajeService);

  fichajes = signal<Fichaje[]>([]);
  cargando = signal(true);
  mesActual = signal(new Date().getMonth() + 1);
  anioActual = signal(new Date().getFullYear());
  fichajeExpandido = signal<number | null>(null);

  nombreMes = computed(() => {
    const fecha = new Date(this.anioActual(), this.mesActual() - 1, 1);
    const texto = fecha.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  });

  constructor() {
    this.cargarFichajes();
  }

  cargarFichajes() {
    this.cargando.set(true);
    this.fichajeService.getMisFichajes(this.mesActual(), this.anioActual()).subscribe({
      next: (data) => {
        this.fichajes.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.fichajes.set([]);
        this.cargando.set(false);
      },
    });
  }

  mesAnterior() {
    if (this.mesActual() === 1) {
      this.mesActual.set(12);
      this.anioActual.set(this.anioActual() - 1);
    } else {
      this.mesActual.set(this.mesActual() - 1);
    }
    this.fichajeExpandido.set(null);
    this.cargarFichajes();
  }

  mesSiguiente() {
    if (this.mesActual() === 12) {
      this.mesActual.set(1);
      this.anioActual.set(this.anioActual() + 1);
    } else {
      this.mesActual.set(this.mesActual() + 1);
    }
    this.fichajeExpandido.set(null);
    this.cargarFichajes();
  }

  toggleExpandir(id: number) {
    this.fichajeExpandido.set(this.fichajeExpandido() === id ? null : id);
  }

  formatearFecha(fecha: string): string {
    const d = this.parsearFechaLocal(fecha);
    return d.toLocaleDateString('es-ES', { weekday: 'long', day: '2-digit', month: '2-digit' });
  }

  formatearHora(fecha: string | null): string {
    if (!fecha) return '--:--';
    const d = this.parsearFechaLocal(fecha);
    return d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  }

  calcularTiempoTrabajado(f: Fichaje): string {
    const entrada = this.parsearFechaLocal(f.fechaEntrada).getTime();
    const salida = f.fechaSalida ? this.parsearFechaLocal(f.fechaSalida).getTime() : Date.now();

    let pausaTotal = 0;
    for (const p of f.pausas) {
      const inicio = this.parsearFechaLocal(p.horaInicio).getTime();
      const fin = p.horaFin ? this.parsearFechaLocal(p.horaFin).getTime() : Date.now();
      pausaTotal += fin - inicio;
    }

    const trabajadoMs = salida - entrada - pausaTotal;
    if (trabajadoMs < 0) return '00:00';
    const horas = Math.floor(trabajadoMs / 3600000);
    const minutos = Math.floor((trabajadoMs % 3600000) / 60000);
    return `${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}`;
  }

  calcularDuracionPausa(p: Pausa): string {
    const inicio = this.parsearFechaLocal(p.horaInicio).getTime();
    const fin = p.horaFin ? this.parsearFechaLocal(p.horaFin).getTime() : Date.now();
    const ms = fin - inicio;
    const minutos = Math.floor(ms / 60000);
    if (minutos < 60) return `${minutos} min`;
    const horas = Math.floor(minutos / 60);
    const mins = minutos % 60;
    return `${horas}h ${mins}min`;
  }

  private parsearFechaLocal(fecha: string): Date {
    if (!fecha.endsWith('Z') && !fecha.includes('+')) {
      const [fechaParte, horaParte] = fecha.split('T');
      const [anio, mes, dia] = fechaParte.split('-').map(Number);
      const [horas, minutos, segundos] = (horaParte || '00:00:00').split(':').map(Number);
      return new Date(anio, mes - 1, dia, horas, minutos, segundos || 0);
    }
    return new Date(fecha);
  }
}
