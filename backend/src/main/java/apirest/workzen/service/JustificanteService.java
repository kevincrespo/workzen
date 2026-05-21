package apirest.workzen.service;

import java.util.List;

import apirest.workzen.modelo.entities.Justificante;

/**
 * Servicio para gestionar justificantes medicos.
 */
public interface JustificanteService {

    /**
     * Obtiene los justificantes de las ausencias de un empleado.
     *
     * @param empleadoId id del empleado
     * @return lista de justificantes
     */
    List<Justificante> obtenerPorEmpleado(int empleadoId);

    /**
     * Obtiene todos los justificantes (para admin).
     *
     * @return lista de todos los justificantes
     */
    List<Justificante> obtenerTodos();

    /**
     * Guarda un justificante nuevo.
     *
     * @param justificante el justificante a guardar
     * @return el justificante guardado
     */
    Justificante guardar(Justificante justificante);

    /**
     * Comprueba si una ausencia ya tiene justificante.
     *
     * @param ausenciaId id de la ausencia
     * @return true si ya tiene justificante
     */
    boolean existePorAusencia(int ausenciaId);

    /**
     * Busca un justificante por su id.
     *
     * @param id id del justificante
     * @return el justificante o null si no existe
     */
    Justificante buscarPorId(int id);

    /**
     * Elimina un justificante por su id.
     *
     * @param id id del justificante
     * @return true si se elimino correctamente
     */
    boolean eliminar(int id);
}
