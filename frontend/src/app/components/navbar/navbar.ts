import { Component, inject, OnInit, OnDestroy, AfterViewInit, ElementRef } from '@angular/core';
import { NgClass } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgClass],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnInit, OnDestroy, AfterViewInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private el = inject(ElementRef);
  private routerSub!: Subscription;

  menuAbierto = false;

  get username(): string {
    return this.authService.getNombre();
  }

  get email(): string {
    return this.authService.getEmail();
  }

  /** El usuario tiene privilegios de administracion (admin o rrhh) */
  get esAdminORecursosHumanos(): boolean {
    return (
      this.authService.tienePrivilegio('admin') ||
      this.authService.tienePrivilegio('rrhh')
    );
  }

  ngOnInit(): void {
    this.routerSub = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.cerrarMenu());
  }

  ngAfterViewInit(): void {
    const submenus = this.el.nativeElement.querySelectorAll('.nav-submenu');
    submenus.forEach((submenu: HTMLElement) => {
      submenu.addEventListener('mouseenter', () => {
        const desplegable = submenu.querySelector('.submenu-desplegable') as HTMLElement;
        if (!desplegable) return;
        submenu.classList.remove('abrir-arriba');
        requestAnimationFrame(() => {
          const rect = desplegable.getBoundingClientRect();
          if (rect.bottom > window.innerHeight) {
            submenu.classList.add('abrir-arriba');
          }
        });
      });
    });
  }

  ngOnDestroy(): void {
    this.routerSub.unsubscribe();
  }

  toggleMenu(): void {
    this.menuAbierto = !this.menuAbierto;
  }

  cerrarMenu(): void {
    this.menuAbierto = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
