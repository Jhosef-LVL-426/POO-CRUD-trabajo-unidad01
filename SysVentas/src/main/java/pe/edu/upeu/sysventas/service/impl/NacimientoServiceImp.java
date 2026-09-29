package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Nacimiento;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.NacimientoRepository;
import pe.edu.upeu.sysventas.service.INacimientoService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class NacimientoServiceImp extends CrudGenericoServiceImp<Nacimiento, Long> implements INacimientoService {

    private final NacimientoRepository nacimientoRepository;

    @Override
    protected ICrudGenericoRepository<Nacimiento, Long> getRepo() {
        return nacimientoRepository;
    }

    // Filtra por texto (recién nacido, padre, madre u hospital) y por rango de fechas
    @Override
    public List<Nacimiento> filtrar(String texto, LocalDate desde, LocalDate hasta) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        return nacimientoRepository.findAll().stream()
                .filter(n -> t.isEmpty()
                        || n.getNombreRecienNacido().toLowerCase().contains(t)
                        || n.getNombrePadre().toLowerCase().contains(t)
                        || n.getNombreMadre().toLowerCase().contains(t)
                        || n.getHospital().toLowerCase().contains(t))
                .filter(n -> desde == null || !n.getFechaNacimiento().isBefore(desde))
                .filter(n -> hasta == null || !n.getFechaNacimiento().isAfter(hasta))
                .toList();
    }
}
