import { Component, inject } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth';

// Componente de layout con sidebar para las páginas protegidas
@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class Layout {
  private router = inject(Router);
  private auth = inject(AuthService);

  // Nombre del usuario logueado
  username = this.auth.getNombre();

  // Cierra sesión y redirige al login
  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
