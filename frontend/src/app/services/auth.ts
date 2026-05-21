import {inject, Inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';

// Respuesta del endpoint /auth/login
interface LoginResponse {
  token: string;
}

// Estructura del contenido del Token
interface TokenPayload {
  sub: string;        // email
  nombre: string;
  privilegios: string[];
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root',
})

export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth/login`;

  http = inject(HttpClient);


  // Descodificar el token
  private decodeToken(): TokenPayload | null {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      return jwtDecode<TokenPayload>(token);
    } catch {
      return null;
    }
  }

  isLogged(): boolean {
    const payload = this.decodeToken();
    if (!payload) return false;

    // Verificar que el token no haya expirado
    const ahora = Math.floor(Date.now() / 1000);
    return payload.exp > ahora;
  }

  login(user: string, pass: string) {
    return this.http
      .post<LoginResponse>(this.apiUrl, { email: user, password: pass })
      .pipe(
        map(res => {
          localStorage.setItem('token', res.token);
          return true;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  getNombre(): string {
    return this.decodeToken()?.nombre || '';
  }

  getEmail(): string {
    return this.decodeToken()?.sub || '';
  }

  getPrivilegios(): string[] {
    return this.decodeToken()?.privilegios || [];
  }

  tienePrivilegio(privilegio: string): boolean {
    return this.getPrivilegios().includes(privilegio);
  }
}
