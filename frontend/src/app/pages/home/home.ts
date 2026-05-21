import { Component, inject, signal, computed, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { NominaService } from '../../services/nomina-service';
import { FichajeService, Fichaje } from '../../services/fichaje-service';
import { CalendarioService, EventoCalendario, CalendarioSemanal } from '../../services/calendario-service';
import { EmpleadoService } from '../../services/empleado-service';
import { ContratoService } from '../../services/contrato-service';
import { FestivoService } from '../../services/festivo-service';
import { NotificacionService } from '../../services/notificacion-service';
import { Festivo } from '../../models/festivo';
import { Notificacion } from '../../models/notificacion';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
  standalone: true,
})
export class Home implements OnInit, OnDestroy {
  private auth = inject(AuthService);
  private nominaService = inject(NominaService);
  private fichajeService = inject(FichajeService);
  private calendarioService = inject(CalendarioService);
  private empleadoService = inject(EmpleadoService);
  private contratoService = inject(ContratoService);
  private festivoService = inject(FestivoService);
  private notificacionService = inject(NotificacionService);

  contarNominas$: Observable<number> = this.nominaService.contarNominas();

  /** Dias de vacaciones disponibles */
  diasDisponibles = signal<number | null>(null);

  /** Contratos activos del empleado */
  contratosActivos = signal<number | null>(null);

  /** Festivos cargados */
  festivos = signal<Festivo[]>([]);

  /** Notificaciones del empleado */
  notificaciones = signal<Notificacion[]>([]);
  cargandoNotificaciones = signal(true);

  /** Estado del calendario semanal */
  semanaActual = signal<string>(this.fechaHoyISO());
  calendarioData = signal<CalendarioSemanal | null>(null);
  cargandoCalendario = signal(false);

  /** Días de la semana calculados a partir de semanaActual */
  diasSemana = computed(() => {
    const data = this.calendarioData();
    if (!data) return [];
    const inicio = new Date(data.semana.inicio + 'T00:00:00');
    const dias: { fecha: string; nombre: string; numero: number; esHoy: boolean }[] = [];
    const hoy = this.fechaHoyISO();
    const nombres = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
    for (let i = 0; i < 7; i++) {
      const d = new Date(inicio);
      d.setDate(d.getDate() + i);
      const fechaStr = this.fechaToISO(d);
      dias.push({
        fecha: fechaStr,
        nombre: nombres[i],
        numero: d.getDate(),
        esHoy: fechaStr === hoy,
      });
    }
    return dias;
  });

  /** Estado del fichaje */
  fichajeActivo = signal<Fichaje | null>(null);
  estadoFichaje = signal<'sin_fichar' | 'fichando' | 'en_pausa'>('sin_fichar');
  cargandoFichaje = signal(true);
  procesandoAccion = signal(false);

  /** Contadores de tiempo formateados */
  tiempoTrabajado = signal('00:00:00');
  tiempoPausa = signal('00:00:00');

  /** Intervalo del contador */
  private intervaloId: ReturnType<typeof setInterval> | null = null;

  get username(): string {
    return this.auth.getNombre();
  }

  ngOnInit(): void {
    this.cargarFichajeActivo();
    this.cargarCalendario();
    this.cargarFestivos();
    this.cargarDiasDisponibles();
    this.cargarContratosActivos();
    this.cargarNotificaciones();
  }

  ngOnDestroy(): void {
    this.detenerIntervalo();
  }

  /** Carga los dias de vacaciones disponibles desde el backend */
  private cargarDiasDisponibles(): void {
    this.empleadoService.getDiasDisponibles().subscribe({
      next: (datos) => this.diasDisponibles.set(datos.diasDisponibles),
      error: () => this.diasDisponibles.set(null),
    });
  }

  /** Carga los contratos activos del empleado desde el backend */
  private cargarContratosActivos(): void {
    this.contratoService.contarActivos().subscribe({
      next: (cantidad) => this.contratosActivos.set(cantidad),
      error: () => this.contratosActivos.set(null),
    });
  }

  /** Carga el fichaje activo desde el backend */
  cargarFichajeActivo(): void {
    this.cargandoFichaje.set(true);
    this.fichajeService.getFichajeActivo().subscribe({
      next: (fichaje) => {
        // 204 No Content llega como null en next
        if (!fichaje) {
          this.fichajeActivo.set(null);
          this.estadoFichaje.set('sin_fichar');
          this.cargandoFichaje.set(false);
          return;
        }
        this.fichajeActivo.set(fichaje);
        this.determinarEstado(fichaje);
        this.iniciarIntervalo();
        this.cargandoFichaje.set(false);
      },
      error: () => {
        this.fichajeActivo.set(null);
        this.estadoFichaje.set('sin_fichar');
        this.cargandoFichaje.set(false);
      },
    });
  }

  /** Determina el estado segun el fichaje y sus pausas */
  private determinarEstado(fichaje: Fichaje): void {
    const pausaAbierta = fichaje.pausas?.find((p) => p.horaFin === null);
    if (pausaAbierta) {
      this.estadoFichaje.set('en_pausa');
    } else {
      this.estadoFichaje.set('fichando');
    }
  }

  /** Iniciar un nuevo fichaje */
  iniciarFichaje(): void {
    this.procesandoAccion.set(true);
    this.fichajeService.iniciarFichaje().subscribe({
      next: (fichaje) => {
        this.fichajeActivo.set(fichaje);
        this.estadoFichaje.set('fichando');
        this.iniciarIntervalo();
        this.procesandoAccion.set(false);
      },
      error: () => {
        this.procesandoAccion.set(false);
      },
    });
  }

  /** Pausar el fichaje activo */
  pausarFichaje(): void {
    const fichaje = this.fichajeActivo();
    if (!fichaje) return;

    this.procesandoAccion.set(true);
    this.fichajeService.pausarFichaje(fichaje.id).subscribe({
      next: () => {
        // Agregar la pausa localmente
        const nuevaPausa = {
          id: 0,
          horaInicio: new Date().toISOString(),
          horaFin: null,
        };
        const fichajeActualizado = {
          ...fichaje,
          pausas: [...(fichaje.pausas || []), nuevaPausa],
        };
        this.fichajeActivo.set(fichajeActualizado);
        this.estadoFichaje.set('en_pausa');
        this.procesandoAccion.set(false);
      },
      error: () => {
        this.procesandoAccion.set(false);
      },
    });
  }

  /** Reanudar el fichaje (finalizar pausa) */
  reanudarFichaje(): void {
    const fichaje = this.fichajeActivo();
    if (!fichaje) return;

    this.procesandoAccion.set(true);
    this.fichajeService.reanudarFichaje(fichaje.id).subscribe({
      next: () => {
        // Cerrar la pausa abierta localmente
        const pausasActualizadas = fichaje.pausas.map((p) =>
          p.horaFin === null ? { ...p, horaFin: new Date().toISOString() } : p
        );
        const fichajeActualizado = {
          ...fichaje,
          pausas: pausasActualizadas,
        };
        this.fichajeActivo.set(fichajeActualizado);
        this.estadoFichaje.set('fichando');
        this.procesandoAccion.set(false);
      },
      error: () => {
        this.procesandoAccion.set(false);
      },
    });
  }

  /** Finalizar el fichaje */
  finalizarFichaje(): void {
    const fichaje = this.fichajeActivo();
    if (!fichaje) return;

    this.procesandoAccion.set(true);
    this.fichajeService.finalizarFichaje(fichaje.id).subscribe({
      next: () => {
        this.fichajeActivo.set(null);
        this.estadoFichaje.set('sin_fichar');
        this.detenerIntervalo();
        this.tiempoTrabajado.set('00:00:00');
        this.tiempoPausa.set('00:00:00');
        this.procesandoAccion.set(false);
      },
      error: () => {
        this.procesandoAccion.set(false);
      },
    });
  }

  /** Inicia el intervalo que actualiza los contadores cada segundo */
  private iniciarIntervalo(): void {
    this.detenerIntervalo();
    this.actualizarContadores();
    this.intervaloId = setInterval(() => this.actualizarContadores(), 1000);
  }

  /** Detiene el intervalo del contador */
  private detenerIntervalo(): void {
    if (this.intervaloId !== null) {
      clearInterval(this.intervaloId);
      this.intervaloId = null;
    }
  }

  /** Calcula y actualiza los contadores de tiempo */
  private actualizarContadores(): void {
    const fichaje = this.fichajeActivo();
    if (!fichaje) return;

    const ahora = new Date().getTime();
    const entrada = this.parsearFechaLocal(fichaje.fechaEntrada);
    const pausas = fichaje.pausas || [];

    // Calcular tiempo total en pausas finalizadas
    let tiempoPausasMs = 0;
    for (const pausa of pausas) {
      if (pausa.horaFin) {
        tiempoPausasMs += this.parsearFechaLocal(pausa.horaFin) - this.parsearFechaLocal(pausa.horaInicio);
      }
    }

    // Buscar pausa activa
    const pausaActiva = pausas.find((p) => p.horaFin === null);

    if (pausaActiva) {
      // En pausa: el tiempo trabajado se congela (hasta el inicio de la pausa)
      const inicioPausa = this.parsearFechaLocal(pausaActiva.horaInicio);
      const trabajadoMs = inicioPausa - entrada - tiempoPausasMs;
      this.tiempoTrabajado.set(this.formatearTiempo(trabajadoMs));

      // Contador de pausa actual
      const pausaActualMs = ahora - inicioPausa;
      this.tiempoPausa.set(this.formatearTiempo(pausaActualMs));
    } else {
      // Fichando: descontar pausas del total
      const trabajadoMs = ahora - entrada - tiempoPausasMs;
      this.tiempoTrabajado.set(this.formatearTiempo(trabajadoMs));
      this.tiempoPausa.set('00:00:00');
    }
  }

  /** Carga los eventos del calendario para la semana actual */
  cargarCalendario(): void {
    this.cargandoCalendario.set(true);
    this.calendarioService.getSemana(this.semanaActual()).subscribe({
      next: (data) => {
        this.calendarioData.set(data);
        this.cargandoCalendario.set(false);
      },
      error: () => {
        this.calendarioData.set(null);
        this.cargandoCalendario.set(false);
      },
    });
  }

  /** Navegar a la semana anterior */
  semanaAnterior(): void {
    const fecha = new Date(this.semanaActual() + 'T00:00:00');
    fecha.setDate(fecha.getDate() - 7);
    this.semanaActual.set(this.fechaToISO(fecha));
    this.cargarCalendario();
  }

  /** Navegar a la semana siguiente */
  semanaSiguiente(): void {
    const fecha = new Date(this.semanaActual() + 'T00:00:00');
    fecha.setDate(fecha.getDate() + 7);
    this.semanaActual.set(this.fechaToISO(fecha));
    this.cargarCalendario();
  }

  /** Volver a la semana actual */
  irAHoy(): void {
    this.semanaActual.set(this.fechaHoyISO());
    this.cargarCalendario();
  }

  /** Carga los festivos desde el backend */
  private cargarFestivos(): void {
    this.festivoService.getFestivos().subscribe({
      next: (datos) => this.festivos.set(datos),
      error: () => this.festivos.set([]),
    });
  }

  /** Obtener festivos de un dia concreto */
  festivosDelDia(fecha: string): Festivo[] {
    return this.festivos().filter((f) => f.fecha === fecha);
  }

  /** Obtener los eventos de un día concreto */
  eventosDelDia(fecha: string): EventoCalendario[] {
    const data = this.calendarioData();
    if (!data) return [];
    return data.eventos.filter((e) => fecha >= e.fechaInicio && fecha <= e.fechaFin);
  }

  /** Carga las notificaciones del empleado desde el backend */
  private cargarNotificaciones(): void {
    this.cargandoNotificaciones.set(true);
    this.notificacionService.getNotificaciones().subscribe({
      next: (datos) => {
        this.notificaciones.set(datos);
        this.cargandoNotificaciones.set(false);
      },
      error: () => {
        this.notificaciones.set([]);
        this.cargandoNotificaciones.set(false);
      },
    });
  }

  /** Marca todas las notificaciones como leidas */
  marcarTodasLeidas(): void {
    this.notificacionService.marcarTodasComoLeidas().subscribe({
      next: () => {
        const actualizadas = this.notificaciones().map((n) => ({ ...n, leida: true }));
        this.notificaciones.set(actualizadas);
      },
    });
  }

  /** Devuelve el icono segun el tipo de notificacion */
  iconoNotificacion(tipo: string): string {
    const iconos: Record<string, string> = {
      nomina_nueva: 'fa-money-bill',
      contrato_nuevo: 'fa-file-contract',
      ausencia_aprobada: 'fa-calendar-check',
      ausencia_rechazada: 'fa-calendar-xmark',
      teletrabajo_aprobado: 'fa-house-laptop',
      teletrabajo_rechazado: 'fa-house-circle-xmark',
      dieta_aprobada: 'fa-utensils',
      dieta_rechazada: 'fa-utensils',
    };
    return iconos[tipo] || 'fa-bell';
  }

  /** Formatea la fecha de la notificacion en formato legible */
  formatearFechaNotificacion(fecha: string): string {
    const d = new Date(fecha);
    const ahora = new Date();
    const diffMs = ahora.getTime() - d.getTime();
    const diffMin = Math.floor(diffMs / 60000);
    const diffHoras = Math.floor(diffMs / 3600000);
    const diffDias = Math.floor(diffMs / 86400000);

    if (diffMin < 1) return 'Ahora mismo';
    if (diffMin < 60) return `Hace ${diffMin} min`;
    if (diffHoras < 24) return `Hace ${diffHoras}h`;
    if (diffDias < 7) return `Hace ${diffDias}d`;
    return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
  }

  /** Devuelve la fecha de hoy en formato YYYY-MM-DD */
  private fechaHoyISO(): string {
    return this.fechaToISO(new Date());
  }

  /** Convierte un Date a string YYYY-MM-DD */
  private fechaToISO(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  /**
   * Parsea una fecha del backend como hora local.
   * Spring devuelve fechas sin zona horaria (ej: "2026-02-28T10:00:00")
   * y new Date() puede interpretarlas como UTC, causando un desfase.
   * Se usa el constructor con componentes para forzar hora local.
   */
  private parsearFechaLocal(fecha: string): number {
    if (!fecha.endsWith('Z') && !fecha.includes('+')) {
      const [fechaParte, horaParte] = fecha.split('T');
      const [anio, mes, dia] = fechaParte.split('-').map(Number);
      const [horas, minutos, segundos] = (horaParte || '00:00:00').split(':').map(Number);
      return new Date(anio, mes - 1, dia, horas, minutos, segundos || 0).getTime();
    }
    return new Date(fecha).getTime();
  }

  /** Formatea milisegundos a HH:MM:SS */
  private formatearTiempo(ms: number): string {
    if (ms < 0) ms = 0;
    const totalSegundos = Math.floor(ms / 1000);
    const horas = Math.floor(totalSegundos / 3600);
    const minutos = Math.floor((totalSegundos % 3600) / 60);
    const segundos = totalSegundos % 60;
    return (
      String(horas).padStart(2, '0') +
      ':' +
      String(minutos).padStart(2, '0') +
      ':' +
      String(segundos).padStart(2, '0')
    );
  }
}
