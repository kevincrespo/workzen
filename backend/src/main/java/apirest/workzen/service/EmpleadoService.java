package apirest.workzen.service;

import apirest.workzen.modelo.dto.CrearEmpleadoDto;
import apirest.workzen.modelo.dto.DiasDisponiblesDto;
import apirest.workzen.modelo.entities.Empleado;

public interface EmpleadoService extends ICrudGenerico<Empleado, Integer> {

    /**
     * Calcula los dias de vacaciones disponibles de un empleado.
     *
     * @param empleado empleado del que calcular los dias
     * @return DTO con dias totales, consumidos y disponibles
     */
    DiasDisponiblesDto calcularDiasDisponibles(Empleado empleado);

    /**
     * Crea un empleado junto con su usuario y privilegios en una transaccion.
     *
     * @param dto datos del empleado, usuario y privilegios a crear
     * @return el empleado creado
     */
    Empleado crearEmpleadoConUsuario(CrearEmpleadoDto dto);
}
