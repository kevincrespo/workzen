package apirest.workzen.service;

import apirest.workzen.modelo.dto.CertificadoDto;

import java.util.List;

public interface CertificadoService {

    List<CertificadoDto> obtenerTodos();

    CertificadoDto obtenerPorId(int certificadoId);

    CertificadoDto crear(CertificadoDto certificadoDto);

    CertificadoDto actualizar(int certificadoId, CertificadoDto certificadoDto);

    void eliminar(int certificadoId);

    List <CertificadoDto> obtenerPorEmpleadoId(int empleadoId);
}
