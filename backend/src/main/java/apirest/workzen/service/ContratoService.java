package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;

import apirest.workzen.modelo.dto.ContratoDto;
import apirest.workzen.modelo.entities.Contrato;

/**
 * Interfaz de servicio para la gestión de contratos
 */
public interface ContratoService {

    /**
     * Crea un contrato
     *
     * @param dto datos del contrato
     * @return contrato creado
     */
    Contrato crearContrato(ContratoDto dto);

    /**
     * Obtiene todos los contratos
     *
     * @return lista de contratos
     */
    List<Contrato> listarTodos();

    /**
     * Obtiene un contrato por su id
     *
     * @param id id del contrato
     * @return contrato encontrado
     */
    Contrato buscarPorId(Integer id);

    /**
     * Obtiene los contratos de un empleado concreto
     *
     * @param empleadoId id del empleado
     * @return lista de contratos
     */
    List<Contrato> listarPorEmpleado(Integer empleadoId);

    /**
     * Busca contratos por nombre o apellidos del empleado
     *
     * @param nombre texto de búsqueda
     * @return lista de contratos
     */
    List<Contrato> listarPorNombreEmpleado(String nombre);

    /**
     * Filtra contratos por tipo (alta, baja, etc.)
     *
     * @param tipo tipo de contrato
     * @return lista de contratos
     */
    List<Contrato> listarPorTipo(String tipo);

    /**
     * Filtra contratos por rango de fechas
     *
     * @param fechaDesde fecha inicial
     * @param fechaHasta fecha final
     * @return lista de contratos
     */
    List<Contrato> listarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta);

    /**
     * Obtiene los contratos de un departamento concreto
     *
     * @param departamentoId id del departamento
     * @return lista de contratos pertenecientes al departamento
     */
    List<Contrato> listarPorDepartamento(Integer departamentoId);

    /**
     * Elimina un contrato por su id
     *
     * @param id id del contrato
     */
    boolean eliminarContrato(Integer id);
}