import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Home } from './pages/home/home';
import { Perfil } from './pages/perfil/perfil';
import { Configuracion } from './pages/configuracion/configuracion';
import { Nominas } from './pages/nominas/nominas';
import { authGuard } from './services/auth-guard';
import { roleGuard } from './services/role-guard';
import { MisAusencias } from './pages/ausencias/mis-ausencias/mis-ausencias';
import { SolicitarAusencia } from './pages/ausencias/solicitar-ausencia/solicitar-ausencia';
import { MisDietas } from './pages/dietas/mis-dietas/mis-dietas';
import { SolicitarDieta } from './pages/dietas/solicitar-dieta/solicitar-dieta';
import { SubirNomina } from './pages/admin/subir-nomina/subir-nomina';
import { GestionarAusencias } from './pages/admin/gestionar-ausencias/gestionar-ausencias';
import { GestionarDietas } from './pages/admin/gestionar-dietas/gestionar-dietas';
import { Empleados } from './pages/admin/empleados/empleados';
import { DetalleEmpleado } from './pages/admin/detalle-empleado/detalle-empleado';
import { SolicitarTeletrabajo } from './pages/teletrabajos/solicitar-teletrabajo/solicitar-teletrabajo';
import { MisTeletrabajos } from './pages/teletrabajos/mis-teletrabajos/mis-teletrabajos';
import { GestionarTeletrabajos } from './pages/admin/gestionar-teletrabajos/gestionar-teletrabajos';
import { Documentacion } from './pages/documentacion/documentacion';
import { SubirDocumento } from './pages/admin/subir-documento/subir-documento';
import { Certificados } from './pages/certificados/certificados';
import { CrearEmpleado } from './pages/admin/crear-empleado/crear-empleado';
import { Contratos } from './pages/contratos/contratos';
import { SubirContrato } from './pages/admin/subir-contrato/subir-contrato';
import { Departamentos } from './pages/admin/departamentos/departamentos';
import { Festivos } from './pages/admin/festivos/festivos';
import { Justificantes } from './pages/justificantes/justificantes';
import { GestionarJustificantes } from './pages/admin/gestionar-justificantes/gestionar-justificantes';
import { MisFichajes } from './pages/fichajes/mis-fichajes/mis-fichajes';
import { GestionarFichajes } from './pages/admin/gestionar-fichajes/gestionar-fichajes';
import { SubirCertificado } from './pages/admin/subir-certificado/subir-certificado';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'nominas', component: Nominas, canActivate: [authGuard] },

  // Ausencias
  { path: 'mis-ausencias', component: MisAusencias, canActivate: [authGuard] },
  { path: 'solicitar-ausencia', component: SolicitarAusencia, canActivate: [authGuard] },
  { path: 'justificantes', component: Justificantes, canActivate: [authGuard] },

  // Teletrabajo
  { path: 'mis-teletrabajos', component: MisTeletrabajos, canActivate: [authGuard] },
  { path: 'solicitar-teletrabajo', component: SolicitarTeletrabajo, canActivate: [authGuard] },

  // Dietas
  { path: 'mis-dietas', component: MisDietas, canActivate: [authGuard] },
  { path: 'solicitar-dieta', component: SolicitarDieta, canActivate: [authGuard] },

  // Fichajes
  { path: 'mis-fichajes', component: MisFichajes, canActivate: [authGuard] },

  // Contratos
  { path: 'contratos', component: Contratos, canActivate: [authGuard] },
  { path: 'perfil', component: Perfil, canActivate: [authGuard] },
  { path: 'configuracion', component: Configuracion, canActivate: [authGuard] },

  // Administracion (solo admin y rrhh)
  {
    path: 'subir-nomina',
    component: SubirNomina,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'subir-contrato',
    component: SubirContrato,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'subir-certificado',
    component: SubirCertificado,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'gestionar-ausencias',
    component: GestionarAusencias,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'gestionar-teletrabajos',
    component: GestionarTeletrabajos,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'gestionar-dietas',
    component: GestionarDietas,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'departamentos',
    component: Departamentos,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'festivos',
    component: Festivos,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'gestionar-fichajes',
    component: GestionarFichajes,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'gestionar-justificantes',
    component: GestionarJustificantes,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'empleados',
    component: Empleados,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'empleados/nuevo',
    component: CrearEmpleado,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  {
    path: 'empleados/:id',
    component: DetalleEmpleado,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },

  // Documentacion
  { path: 'documentacion', component: Documentacion, canActivate: [authGuard] },

  // Documentos
  {
    path: 'subir-documento',
    component: SubirDocumento,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['admin', 'rrhh'] },
  },
  { path: 'certificados', component: Certificados, canActivate: [authGuard] },

  // Cualquier otra ruta
  { path: '**', redirectTo: 'login' },
];
