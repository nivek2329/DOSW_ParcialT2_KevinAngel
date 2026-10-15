package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.controller.mappers.ReservaMapper;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Disponibilidad;
import edu.dosw.parcial.core.models.Especialidad;
import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.core.models.Reserva;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.validators.ReservaValidationChain;
import edu.dosw.parcial.core.validators.ReservaValidationContext;
import edu.dosw.parcial.persistence.entities.FranjaQuotaDocument;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.ReservaEntityMapper;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ReservaValidationChain validationChain;

    @Spy
    private ReservaMapper reservaMapper;

    @Spy
    private ReservaEntityMapper reservaEntityMapper;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    void pedirTurnoSuccessCalculatesTotalAndOccupiesSlot() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos Gomez", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001", "SER-004"));

        doAnswer(invocation -> {
            ReservaValidationContext ctx = invocation.getArgument(0);
            Turno turno = new Turno("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
            Barbero barbero = new Barbero("barb-1", "Miguel", List.of(Especialidad.CORTE, Especialidad.BARBA), new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(17, 0)));
            List<Servicio> servicios = List.of(
                    new Servicio("SER-001", "Corte clasico", BigDecimal.valueOf(25000), Especialidad.CORTE, true),
                    new Servicio("SER-004", "Afeitado con navaja", BigDecimal.valueOf(20000), Especialidad.BARBA, true)
            );
            ctx.setTurnoSeleccionado(turno);
            ctx.setBarberoAsignado(barbero);
            ctx.setCatalogoServicios(servicios);
            return null;
        }).when(validationChain).executeChain(any(ReservaValidationContext.class));

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        FranjaQuotaDocument quotaDoc = new FranjaQuotaDocument("2026-10-20_10:00", fecha, hora, 1);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranjaQuotaDocument.class)))
                .thenReturn(quotaDoc);

        TurnoDocument turnoOcupado = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.OCUPADO);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(TurnoDocument.class)))
                .thenReturn(turnoOcupado);

        ReservaDocument savedReservaDoc = new ReservaDocument("res-100", "trn-1", "carlos@gmail.com", "Carlos Gomez", "3101234567",
                "barb-1", fecha, hora, List.of("SER-001", "SER-004"), BigDecimal.valueOf(45000), EstadoReserva.CONFIRMADA, LocalDateTime.now());
        when(reservaRepository.save(any(ReservaDocument.class))).thenReturn(savedReservaDoc);

        Reserva resultado = reservaService.pedirTurno(req, "carlos@gmail.com");

        assertNotNull(resultado);
        assertEquals("res-100", resultado.getId());
        assertEquals("trn-1", resultado.getTurnoId());
        assertEquals(BigDecimal.valueOf(45000), resultado.getPrecioTotal());
        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());

        verify(mongoTemplate).upsert(any(Query.class), any(Update.class), eq(FranjaQuotaDocument.class));
        verify(reservaRepository).save(any(ReservaDocument.class));
    }

    @Test
    void pedirTurnoFailsOnValidationDoesNotModifyTurnoNorCreateReserva() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos Gomez", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));

        doThrow(new ConflictException("E01", "Franja llena"))
                .when(validationChain).executeChain(any(ReservaValidationContext.class));

        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.pedirTurno(req, "carlos@gmail.com"));

        assertEquals("E01", ex.getCodigo());
        verify(mongoTemplate, never()).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranjaQuotaDocument.class));
        verify(mongoTemplate, never()).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(TurnoDocument.class));
        verify(reservaRepository, never()).save(any(ReservaDocument.class));
    }

    @Test
    void pedirTurnoThrowsConflictWhenConcurrentQuotaExceeded() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos Gomez", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));

        doAnswer(invocation -> {
            ReservaValidationContext ctx = invocation.getArgument(0);
            Turno turno = new Turno("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
            ctx.setTurnoSeleccionado(turno);
            return null;
        }).when(validationChain).executeChain(any(ReservaValidationContext.class));

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(2L);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranjaQuotaDocument.class)))
                .thenReturn(null);

        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.pedirTurno(req, "carlos@gmail.com"));

        assertEquals("E01", ex.getCodigo());
        verify(mongoTemplate, never()).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(TurnoDocument.class));
        verify(reservaRepository, never()).save(any(ReservaDocument.class));
    }

    @Test
    void pedirTurnoThrowsConflictWhenConcurrentInstanceAlreadyOccupiedSlot() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos Gomez", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));

        doAnswer(invocation -> {
            ReservaValidationContext ctx = invocation.getArgument(0);
            Turno turno = new Turno("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
            ctx.setTurnoSeleccionado(turno);
            return null;
        }).when(validationChain).executeChain(any(ReservaValidationContext.class));

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        FranjaQuotaDocument quotaDoc = new FranjaQuotaDocument("2026-10-20_10:00", fecha, hora, 1);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranjaQuotaDocument.class)))
                .thenReturn(quotaDoc);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(TurnoDocument.class)))
                .thenReturn(null);

        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.pedirTurno(req, "carlos@gmail.com"));

        assertEquals("E01", ex.getCodigo());
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(FranjaQuotaDocument.class));
        verify(reservaRepository, never()).save(any(ReservaDocument.class));
    }

    @Test
    void pedirTurnoRollbackWhenReservaSaveFailsReleasesTurnoToDisponible() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(10, 0);
        ReservaRequestDTO req = new ReservaRequestDTO("Carlos Gomez", "3101234567", "carlos@gmail.com", "barb-1", fecha, hora, List.of("SER-001"));

        doAnswer(invocation -> {
            ReservaValidationContext ctx = invocation.getArgument(0);
            Turno turno = new Turno("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
            Barbero barbero = new Barbero("barb-1", "Miguel", List.of(Especialidad.CORTE), new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(9, 0), LocalTime.of(17, 0)));
            ctx.setTurnoSeleccionado(turno);
            ctx.setBarberoAsignado(barbero);
            ctx.setCatalogoServicios(List.of(new Servicio("SER-001", "Corte", BigDecimal.valueOf(25000), Especialidad.CORTE, true)));
            return null;
        }).when(validationChain).executeChain(any(ReservaValidationContext.class));

        when(reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA)).thenReturn(0L);

        FranjaQuotaDocument quotaDoc = new FranjaQuotaDocument("2026-10-20_10:00", fecha, hora, 1);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranjaQuotaDocument.class)))
                .thenReturn(quotaDoc);

        TurnoDocument turnoOcupado = new TurnoDocument("trn-1", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.OCUPADO);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(TurnoDocument.class)))
                .thenReturn(turnoOcupado);

        when(reservaRepository.save(any(ReservaDocument.class))).thenThrow(new RuntimeException("Error simulado de conexion con MongoDB"));

        assertThrows(RuntimeException.class, () -> reservaService.pedirTurno(req, "carlos@gmail.com"));

        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(any(Query.class), updateCaptor.capture(), eq(TurnoDocument.class));
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(FranjaQuotaDocument.class));

        Update capturedUpdate = updateCaptor.getValue();
        assertEquals(EstadoTurno.DISPONIBLE, capturedUpdate.getUpdateObject().get("estado"));
    }

    @Test
    void consultarMisReservasReturnsOnlyClientReservationsOrdered() {
        String clientEmail = "carlos@gmail.com";
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(11, 0);

        ReservaDocument r1 = new ReservaDocument("res-1", "trn-1", clientEmail, "Carlos", "3101234567", "barb-1",
                fecha, hora, List.of("SER-001"), BigDecimal.valueOf(25000), EstadoReserva.CONFIRMADA, LocalDateTime.now());

        when(reservaRepository.findByClienteEmailOrderByFechaDescHoraInicioDesc(clientEmail)).thenReturn(List.of(r1));

        List<Reserva> misReservas = reservaService.consultarMisReservas(clientEmail);

        assertNotNull(misReservas);
        assertEquals(1, misReservas.size());
        assertEquals("res-1", misReservas.get(0).getId());
        assertEquals(clientEmail, misReservas.get(0).getClienteEmail());
        verify(reservaRepository).findByClienteEmailOrderByFechaDescHoraInicioDesc(clientEmail);
    }

    @Test
    void consultarTodasLasReservasReturnsAllSystemReservations() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(11, 0);

        ReservaDocument r1 = new ReservaDocument("res-1", "trn-1", "carlos@gmail.com", "Carlos", "3101234567", "barb-1",
                fecha, hora, List.of("SER-001"), BigDecimal.valueOf(25000), EstadoReserva.CONFIRMADA, LocalDateTime.now());
        ReservaDocument r2 = new ReservaDocument("res-2", "trn-2", "ana@gmail.com", "Ana", "3119876543", "barb-2",
                fecha, hora.plusHours(1), List.of("SER-003"), BigDecimal.valueOf(15000), EstadoReserva.CONFIRMADA, LocalDateTime.now());

        when(reservaRepository.findAllByOrderByFechaDescHoraInicioDesc()).thenReturn(List.of(r1, r2));

        List<Reserva> todas = reservaService.consultarTodasLasReservas();

        assertNotNull(todas);
        assertEquals(2, todas.size());
        verify(reservaRepository).findAllByOrderByFechaDescHoraInicioDesc();
    }
}
