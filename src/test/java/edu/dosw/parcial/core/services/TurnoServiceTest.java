package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.TurnoCreateDTO;
import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.controller.handlers.ResourceNotFoundException;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.controller.mappers.TurnoMapper;
import edu.dosw.parcial.core.models.Disponibilidad;
import edu.dosw.parcial.core.models.Especialidad;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private BarberoRepository barberoRepository;

    @Spy
    private TurnoMapper turnoMapper;

    @Spy
    private TurnoEntityMapper turnoEntityMapper;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    @Test
    void crearTurnoSuccess() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        DayOfWeek dia = fecha.getDayOfWeek();
        LocalTime hora = LocalTime.of(11, 0);

        TurnoCreateDTO request = new TurnoCreateDTO("barb-1", fecha, hora);
        Disponibilidad disp = new Disponibilidad(Set.of(dia), LocalTime.of(10, 0), LocalTime.of(17, 0));
        BarberoDocument barbero = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);

        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barbero));
        when(turnoRepository.findByBarberoIdAndFechaAndHoraInicio("barb-1", fecha, hora)).thenReturn(Optional.empty());

        TurnoDocument savedDoc = new TurnoDocument("trn-100", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.save(any(TurnoDocument.class))).thenReturn(savedDoc);

        Turno result = turnoService.crearTurno(request);

        assertNotNull(result);
        assertEquals("trn-100", result.getId());
        assertEquals("barb-1", result.getBarberoId());
        assertEquals(EstadoTurno.DISPONIBLE, result.getEstado());
        verify(turnoRepository).save(any(TurnoDocument.class));
    }

    @Test
    void crearTurnoThrowsWhenBarberoNotFound() {
        TurnoCreateDTO request = new TurnoCreateDTO("inexistente", LocalDate.now().plusDays(2), LocalTime.of(11, 0));
        when(barberoRepository.findById("inexistente")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> turnoService.crearTurno(request));

        assertEquals("E404-BARBERO", ex.getCodigo());
        verify(turnoRepository, never()).save(any(TurnoDocument.class));
    }

    @Test
    void crearTurnoThrowsWhenDateNotFuture() {
        TurnoCreateDTO request = new TurnoCreateDTO("barb-1", LocalDate.now().minusDays(1), LocalTime.of(11, 0));
        Disponibilidad disp = new Disponibilidad(Set.of(DayOfWeek.MONDAY), LocalTime.of(10, 0), LocalTime.of(17, 0));
        BarberoDocument barbero = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);

        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barbero));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> turnoService.crearTurno(request));

        assertEquals("E400-FECHA", ex.getCodigo());
        verify(turnoRepository, never()).save(any(TurnoDocument.class));
    }

    @Test
    void crearTurnoThrowsWhenOutsideBarberoSchedule() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        TurnoCreateDTO request = new TurnoCreateDTO("barb-1", fecha, LocalTime.of(20, 0));
        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(10, 0), LocalTime.of(17, 0));
        BarberoDocument barbero = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);

        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barbero));

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> turnoService.crearTurno(request));

        assertEquals("E422-HORARIO", ex.getCodigo());
        verify(turnoRepository, never()).save(any(TurnoDocument.class));
    }

    @Test
    void crearTurnoThrowsWhenDuplicateSlot() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(11, 0);
        TurnoCreateDTO request = new TurnoCreateDTO("barb-1", fecha, hora);
        Disponibilidad disp = new Disponibilidad(Set.of(fecha.getDayOfWeek()), LocalTime.of(10, 0), LocalTime.of(17, 0));
        BarberoDocument barbero = new BarberoDocument("barb-1", "Miguel", List.of(Especialidad.CORTE), disp);

        when(barberoRepository.findById("barb-1")).thenReturn(Optional.of(barbero));
        TurnoDocument existente = new TurnoDocument("trn-existente", "barb-1", fecha, hora, hora.plusHours(1), EstadoTurno.DISPONIBLE);
        when(turnoRepository.findByBarberoIdAndFechaAndHoraInicio("barb-1", fecha, hora)).thenReturn(Optional.of(existente));

        ConflictException ex = assertThrows(ConflictException.class, () -> turnoService.crearTurno(request));

        assertEquals("E409-DUPLICADO", ex.getCodigo());
        verify(turnoRepository, never()).save(any(TurnoDocument.class));
    }
}
