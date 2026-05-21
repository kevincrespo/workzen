package apirest.workzen.service;

import apirest.workzen.modelo.dto.CertificadoDto;
import apirest.workzen.modelo.entities.Certificado;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.repository.CertificadoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificadoServiceImpl implements CertificadoService{

    @Autowired
    CertificadoRepository certificadoRepository;

    @Autowired
    EmpleadoRepository empleadoRepository;


    @Override
    public List<CertificadoDto> obtenerTodos() {
        return certificadoRepository.findAll()
                .stream()
                .map(this::mapToCertificadoDto)
                .toList();
    }

    @Override
    public CertificadoDto obtenerPorId(int certificadoId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado con ID: " + certificadoId));
        return mapToCertificadoDto(certificado);
    }

    @Override
    public CertificadoDto crear(CertificadoDto certificadoDto) {
        //Obtenemos el empleado asociado
        Empleado empleado = empleadoRepository.findById(certificadoDto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " +
                        certificadoDto.getEmpleadoId()));

        Certificado certificado = Certificado.builder()
                .empleado(empleado)
                .nombre(certificadoDto.getNombre())
                .fecha(certificadoDto.getFecha())
                .archivo(certificadoDto.getArchivo())
                .build();

        return mapToCertificadoDto(certificadoRepository.save(certificado));
    }

    @Override
    public CertificadoDto actualizar(int certificadoId, CertificadoDto dto) {

        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado con id: " + certificadoId));

        certificado.setArchivo(dto.getArchivo());
        certificado.setNombre(dto.getNombre());
        certificado.setFecha(dto.getFecha());

        certificadoRepository.save(certificado);

        return mapToCertificadoDto(certificado);
    }

    @Override
    public void eliminar(int certificadoId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                        .orElseThrow(() -> new RuntimeException("Certificado no encontrado con ID: " + certificadoId));

        certificadoRepository.delete(certificado);
    }

    @Override
    public List<CertificadoDto> obtenerPorEmpleadoId(int empleadoId) {

        return certificadoRepository.findByEmpleadoId(empleadoId)
                .stream()
                .map(this::mapToCertificadoDto)
                .toList();
    }

    // Mapeo a DTO
    private CertificadoDto mapToCertificadoDto(Certificado certificado) {
        return CertificadoDto.builder()
                .certificadoId(certificado.getId())
                .empleadoId(certificado.getEmpleado().getId())
                .empleadoNombre(certificado.getEmpleado().getNombre() + " " +
                        certificado.getEmpleado().getApellido1() + " " +
                        certificado.getEmpleado().getApellido2())
                .nombre(certificado.getNombre())
                .fecha(certificado.getFecha())
                .archivo(certificado.getArchivo())
                .build();
    }
}
