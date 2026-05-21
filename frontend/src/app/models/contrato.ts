import { Empleado } from '../models/empleado';

export interface Contrato {
  id: number;
  empleado: Empleado;  
  tipo: string;        
  salario: number;
  fecha: Date;         
  archivo: string;     
}