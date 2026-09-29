package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.model.Nacimiento;

import java.time.LocalDate;
import java.util.List;

public interface INacimientoService extends ICrudGenericoService<Nacimiento, Long> {
    List<Nacimiento> filtrar(String texto, LocalDate desde, LocalDate hasta);
}
