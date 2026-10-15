package edu.dosw.parcial.config;

import edu.dosw.parcial.core.models.Disponibilidad;
import edu.dosw.parcial.core.models.Especialidad;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDataLoader.class);

    private final UsuarioRepository usuarioRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseDataLoader(UsuarioRepository usuarioRepository,
                              BarberoRepository barberoRepository,
                              ServicioRepository servicioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            seedUsuarios();
            seedBarberos();
            seedServicios();
        } catch (Exception ex) {
            log.warn("No se pudo conectar con MongoDB para sembrado inicial automatico: {}", ex.getMessage());
        }
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() == 0) {
            UsuarioDocument admin = new UsuarioDocument(
                    "usr-admin",
                    "Administrador",
                    "admin@barber.com",
                    "3100000000",
                    passwordEncoder.encode("admin123"),
                    Rol.ADMIN
            );
            UsuarioDocument cliente = new UsuarioDocument(
                    "usr-cliente",
                    "Cliente Prueba",
                    "cliente@gmail.com",
                    "3111111111",
                    passwordEncoder.encode("cliente123"),
                    Rol.CLIENTE
            );
            usuarioRepository.saveAll(List.of(admin, cliente));
            log.info("Usuarios iniciales creados: admin@barber.com y cliente@gmail.com");
        }
    }

    private void seedBarberos() {
        if (barberoRepository.count() == 0) {
            Set<DayOfWeek> diasLaborales = Set.of(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
            );
            Disponibilidad disp = new Disponibilidad(diasLaborales, LocalTime.of(8, 0), LocalTime.of(18, 0));

            BarberoDocument b1 = new BarberoDocument("barb-1", "Miguel Angel", List.of(Especialidad.CORTE, Especialidad.BARBA), disp);
            BarberoDocument b2 = new BarberoDocument("barb-2", "Carlos Gomez", List.of(Especialidad.CORTE, Especialidad.CAPILAR), disp);
            barberoRepository.saveAll(List.of(b1, b2));
            log.info("Barberos iniciales creados: barb-1 y barb-2");
        }
    }

    private void seedServicios() {
        if (servicioRepository.count() == 0) {
            ServicioDocument s1 = new ServicioDocument("srv-1", "SER-001", "Corte Clasico", BigDecimal.valueOf(25000), Especialidad.CORTE, true);
            ServicioDocument s2 = new ServicioDocument("srv-2", "SER-002", "Afeitado y Barba", BigDecimal.valueOf(20000), Especialidad.BARBA, true);
            ServicioDocument s3 = new ServicioDocument("srv-3", "SER-003", "Tratamiento Capilar", BigDecimal.valueOf(40000), Especialidad.CAPILAR, true);
            servicioRepository.saveAll(List.of(s1, s2, s3));
            log.info("Catalogo de servicios inicial creado: SER-001, SER-002, SER-003");
        }
    }
}
