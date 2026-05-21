import {Component, inject} from '@angular/core';
import {NominaService} from '../../services/nomina-service';
import {AuthService} from '../../services/auth';
import {AsyncPipe, DatePipe} from '@angular/common';

@Component({
  selector: 'app-nominas',
  imports: [AsyncPipe, DatePipe],
  templateUrl: './nominas.html',
  styleUrl: './nominas.css',
})

export class Nominas {
  private nominaService = inject(NominaService);
  private authService = inject(AuthService);
  nominas$ = this.nominaService.getNominas(new Date().getFullYear());

  // Listado de años para el select
  years = () => {
    let yearList: number[] = []
    let actual = new Date().getFullYear();

    for (let i = 0; i < 3; i++) {
      yearList.push(actual - i);
    }
    return yearList;
  }

  // Al cambiar de año, recargar nominas
  changeYear(e: Event) {
    let year = Number((e.target as HTMLSelectElement).value);
    this.nominas$ = this.nominaService.getNominas(year);
  }

  /** Descargar el archivo de la nomina. Nombre: Nomina_Juan_28022025.pdf */
  descargar(id: number, fecha: Date): void {
    this.nominaService.descargarNomina(id).subscribe(blob => {
      const nombre = this.authService.getNombre();
      const d = new Date(fecha);
      const dia = String(d.getDate()).padStart(2, '0');
      const mes = String(d.getMonth() + 1).padStart(2, '0');
      const anio = d.getFullYear();
      const nombreArchivo = `Nomina_${nombre}_${dia}${mes}${anio}.pdf`;

      const url = window.URL.createObjectURL(blob);
      const enlace = document.createElement('a');
      enlace.href = url;
      enlace.download = nombreArchivo;
      enlace.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
