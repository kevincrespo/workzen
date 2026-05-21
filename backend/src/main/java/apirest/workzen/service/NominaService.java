package apirest.workzen.service;

import java.util.List;

import apirest.workzen.modelo.entities.Nomina;

public interface NominaService extends ICrudGenerico<Nomina, Integer> {
    int findByEmpleadoId(int empleadoId);
    List<Nomina> findByEmpleadoIdAndYear(int empleadoId, int year);
}
