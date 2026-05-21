package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.dto.ContratoDto;
import apirest.workzen.modelo.entities.Contrato;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.repository.ContratoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public Contrato crearContrato(ContratoDto dto) {
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId()).orElse(null);
        if (empleado == null) {
            return null;
        }

        Contrato contrato = new Contrato();
        contrato.setEmpleado(empleado);
        contrato.setTipo(dto.getTipo());
        contrato.setSalario(dto.getSalario());
        contrato.setFecha(dto.getFecha());
        contrato.setArchivo(dto.getArchivo());

        return contratoRepository.save(contrato);
    }

    @Override
    public List<Contrato> listarTodos() {
        return contratoRepository.findAll();
    }

    @Override
    public Contrato buscarPorId(Integer id) {
        return contratoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Contrato> listarPorEmpleado(Integer empleadoId) {
        return contratoRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<Contrato> listarPorNombreEmpleado(String nombre) {
        return contratoRepository.findByEmpleadoNombre(nombre);
    }

    @Override
    public List<Contrato> listarPorTipo(String tipo) {
        try {
            Contrato.Tipo tipoEnum = Contrato.Tipo.valueOf(tipo.toLowerCase());
            return contratoRepository.findByTipo(tipoEnum);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public List<Contrato> listarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        return contratoRepository.findByFechaBetween(fechaDesde, fechaHasta);
    }

    @Override
    public List<Contrato> listarPorDepartamento(Integer departamentoId) {
        return contratoRepository.findByEmpleadoDepartamentoId(departamentoId);
    }

    @Override
    public boolean eliminarContrato(Integer id) {
        if (contratoRepository.existsById(id)) {
            contratoRepository.deleteById(id);
            return true;
        }
        return  false;
    }
}