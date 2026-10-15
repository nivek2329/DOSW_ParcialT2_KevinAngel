package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.controller.handlers.ResourceNotFoundException;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.core.models.Disponibilidad;
import edu.dosw.parcial.core.models.Especialidad;
import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaValidationChainTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private BarberoRepository barberoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Spy
    private TurnoEntityMapper turnoEntityMapper;

    private ReservaValidationChain validationChain;

    @BeforeEach
    void setUp() {
        FranjaDisponibilidadValidator v1 = new FranjaDisponibilidadValidator(reservaRepository, turnoRepository);
        BarberoDisponibleValidator v2 = new BarberoDisponibleValidator(barberoRepository, servicioRepository, turnoEntityMapper);
        DatosClienteValidator v3 = new DatosClienteValidator();
        ServicioPermitidoValidator v4 = new ServicioPermitidoValidator(servicioRepository);

        validationChain = new ReservaValidationChain(List.of(v1, v2, v3, v4));
    }

    @Test
    void chainFailsOnE01WhenSlotAlreadyHasThreeBookings() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(3L);

        ConflictException ex = assertThrows(ConflictException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E01", ex.getCodigo());
        verifyNoInteractions(barberoRepository);
    }

    @Test
    void chainFailsWith404WhenPreferredBarberoDoesNotExist() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@gmail.com", "barb-fantasma", fecha, hora, List.of("SER-001"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(barberoRepository.findById("barb-fantasma")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E100", ex.getCodigo());
    }

    @Test
    void chainAssignsBarberoAutomaticallyMatchingRequiredSpecialty() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@gmail.com", null, fecha, hora, List.of("SER-003"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(1L);

        TurnoDocument turnoMiguel = new TurnoDocument("trn-miguel", "barb-miguel", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        TurnoDocument turnoLaura = new TurnoDocument("trn-laura", "barb-laura", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoMiguel, turnoLaura));

        when(servicioRepository.findByCodigo("SER-003")).thenReturn(Optional.of(
                new ServicioDocument("s3", "SER-003", "Diseno de cejas", BigDecimal.valueOf(15000), Especialidad.CEJAS, true)
        ));

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument docMiguel = new BarberoDocument("barb-miguel", "Miguel", List.of(Especialidad.CORTE), disp);
        BarberoDocument docLaura = new BarberoDocument("barb-laura", "Laura", List.of(Especialidad.CEJAS, Especialidad.CAPILAR), disp);

        when(barberoRepository.findById("barb-miguel")).thenReturn(Optional.of(docMiguel));
        when(barberoRepository.findById("barb-laura")).thenReturn(Optional.of(docLaura));

        when(servicioRepository.findByActivoTrue()).thenReturn(List.of(
                new ServicioDocument("s3", "SER-003", "Diseno de cejas", BigDecimal.valueOf(15000), Especialidad.CEJAS, true)
        ));

        validationChain.executeChain(ctx);

        assertNotNull(ctx.getBarberoAsignado());
        assertEquals("barb-laura", ctx.getBarberoAsignado().getId());
        assertEquals("trn-laura", ctx.getTurnoSeleccionado().getId());
    }

    @Test
    void chainFailsOnE02WhenBarberoLacksRequiredSpecialty() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-005"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(1L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(servicioRepository.findByCodigo("SER-005")).thenReturn(Optional.of(
                new ServicioDocument("s5", "SER-005", "Tratamiento capilar", BigDecimal.valueOf(45000), Especialidad.CAPILAR, true)
        ));

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument barberoDoc = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);
        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barberoDoc));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E02", ex.getCodigo());
    }

    @Test
    void chainFailsOnE03WhenNameIsEmpty() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("   ", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(
                new ServicioDocument("s1", "SER-001", "Corte", BigDecimal.valueOf(25000), Especialidad.CORTE, true)
        ));

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument barberoDoc = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);
        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barberoDoc));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E03", ex.getCodigo());
    }

    @Test
    void chainFailsOnE07WhenPhoneNotTenDigits() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "12345", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(
                new ServicioDocument("s1", "SER-001", "Corte", BigDecimal.valueOf(25000), Especialidad.CORTE, true)
        ));

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument barberoDoc = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);
        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barberoDoc));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E07", ex.getCodigo());
    }

    @Test
    void chainFailsOnE08WhenEmailDomainNotGmail() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@hotmail.com", "barb-1", fecha, hora, List.of("SER-001"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@hotmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(
                new ServicioDocument("s1", "SER-001", "Corte", BigDecimal.valueOf(25000), Especialidad.CORTE, true)
        ));

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument barberoDoc = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);
        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barberoDoc));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E08", ex.getCodigo());
    }

    @Test
    void chainFailsOnE09With404WhenServiceNotInActiveCatalog() {
        LocalDate fecha = LocalDate.now().plusDays(3);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-999"));
        ReservaValidationContext ctx = new ReservaValidationContext(req, "carlos@gmail.com");

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        TurnoDocument turnoDoc = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE)).thenReturn(List.of(turnoDoc));

        when(servicioRepository.findByCodigo("SER-999")).thenReturn(Optional.empty());

        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(18, 0));
        BarberoDocument barberoDoc = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);
        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barberoDoc));

        when(servicioRepository.findByActivoTrue()).thenReturn(List.of(
                new ServicioDocument("s1", "SER-001", "Corte clasico", BigDecimal.valueOf(25000), Especialidad.CORTE, true)
        ));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> validationChain.executeChain(ctx));

        assertEquals("E09", ex.getCodigo());
    }
}
