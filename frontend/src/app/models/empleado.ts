/** Datos basicos del empleado para listados y selects */
export interface Empleado {
  id: number;
  nombre: string;
  apellido1: string;
  apellido2: string;
}

/** Empleado resumido para el listado de admin (incluye departamento) */
export interface EmpleadoResumen {
  id: number;
  nombre: string;
  apellido1: string;
  apellido2: string;
  departamento: string;
}

/** Empleado completo para la ficha de detalle */
export interface EmpleadoDetalle {
  id: number;
  nombre: string;
  apellido1: string;
  apellido2: string;
  nif: string;
  numeroSs: string;
  direccion: string;
  provincia: string;
  localidad: string;
  codigoPostal: number;
  fechaNacimiento: string;
  iban: string;
  telefono: string;
  departamentoId: number;
  departamento: string;
}

/** Datos editables del empleado para el PUT */
export interface EmpleadoEdicion {
  nombre: string;
  apellido1: string;
  apellido2: string;
  nif: string;
  numeroSs: string;
  direccion: string;
  provincia: string;
  localidad: string;
  codigoPostal: number;
  fechaNacimiento: string;
  iban: string;
  telefono: string;
  departamentoId: number;
}

/** Datos para crear un empleado nuevo (POST /empleados) */
export interface EmpleadoCreacion {
  email: string;
  password: string;
  privilegios: string[];
  nombre: string;
  apellido1: string;
  apellido2: string;
  nif: string;
  numeroSs: string;
  direccion: string;
  provincia: string;
  localidad: string;
  codigoPostal: number;
  fechaNacimiento: string;
  iban: string;
  telefono: string;
  departamentoId: number;
}

/** Departamento para el select */
export interface Departamento {
  id: number;
  nombre: string;
}
