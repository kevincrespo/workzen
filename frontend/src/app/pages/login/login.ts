import { Component, inject} from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

interface LoginForm {
  user: FormControl<string>;
  pass: FormControl<string>;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})

export class Login {

  private auth = inject(AuthService);
  private router = inject(Router);

  mensaje = '';

  loginForm = new FormGroup<LoginForm>({
    user: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    pass: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });


  // Si ya está logeado, devolverle a home
  ngOnInit() {
    if (this.auth.isLogged())
      this.router.navigate(['/home']);
  }

  // Manejar el evento de login
  login($event?: KeyboardEvent) {
    if ($event && $event.key !== 'Enter')
      return;

    if (this.loginForm.invalid)
      return;

    const { user, pass } = this.loginForm.getRawValue();

    // Nos suscribimos al Observable del login
    this.auth.login(user, pass).subscribe({
      next: () => this.router.navigate(['/home']),
      error: () => this.mensaje = 'Usuario o contraseña incorrectos'
    });
  }
}
