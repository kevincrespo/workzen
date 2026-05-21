import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth';

/**
 * Guard que protege rutas segun los privilegios del usuario.
 * Los roles permitidos se definen en el campo 'data.roles' de la ruta.
 * Si el usuario no tiene ninguno de los roles, redirige a /home.
 *
 * Ejemplo de uso en rutas:
 *   { path: 'admin', component: ..., canActivate: [authGuard, roleGuard], data: { roles: ['admin', 'rrhh'] } }
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Obtener los roles permitidos desde la configuracion de la ruta
  const rolesPermitidos: string[] = route.data['roles'] || [];

  // Comprobar si el usuario tiene al menos uno de los roles permitidos
  const tieneAcceso = rolesPermitidos.some(rol => auth.tienePrivilegio(rol));

  if (!tieneAcceso) {
    router.navigate(['/home']);
    return false;
  }

  return true;
};