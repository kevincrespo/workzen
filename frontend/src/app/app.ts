import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { AuthService } from './services/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true,
})
export class App {
  private authService = inject(AuthService);

  get isLogged(): boolean {
    return this.authService.isLogged();
  }
}
