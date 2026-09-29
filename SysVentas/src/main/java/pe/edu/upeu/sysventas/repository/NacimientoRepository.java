package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Nacimiento;

import java.time.LocalDate;

public class NacimientoRepository extends AbstractJpaRepository<Nacimiento, Long> {
    private long sequence = 1;

    // Se cargan dos registros de ejemplo al iniciar la aplicación
    public NacimientoRepository() {
        seedData();
    }

    @Override
    protected Long getId(Nacimiento entity) {
        return entity.getIdNacimiento();
    }

    @Override
    protected void setId(Nacimiento entity, Long id) {
        entity.setIdNacimiento(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    public void seedData() {
        save(new Nacimiento(null, "Sofía Mamani Quispe", LocalDate.of(2026, 9, 12),
                "Hospital Goyeneche", "Luis Mamani Condori", "Rosa Quispe Huamán"));
        save(new Nacimiento(null, "Mateo Vargas Flores", LocalDate.of(2026, 9, 20),
                "Hospital Honorio Delgado", "Carlos Vargas Ramos", "Ana Flores Paredes"));
    }
}
