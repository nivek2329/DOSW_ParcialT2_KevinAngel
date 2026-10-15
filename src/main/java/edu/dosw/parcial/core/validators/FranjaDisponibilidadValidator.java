package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class FranjaDisponibilidadValidator implements ReservaValidator {

    private final ReservaRepository reservaRepository;
    private final TurnoRepository turnoRepository;

    public FranjaDisponibilidadValidator(ReservaRepository reservaRepository,
                                         TurnoRepository turnoRepository) {
        this.reservaRepository = reservaRepository;
        this.turnoRepository = turnoRepository;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void validate(ReservaValidationContext context) {
        LocalDate fecha = context.getRequest().getFecha();
        LocalTime hora = context.getRequest().getHoraInicio();

        long reservasActivas = reservaRepository.countByFechaAndHoraInicioAndEstado(fecha, hora, EstadoReserva.CONFIRMADA);
        if (reservasActivas >= 3) {
            throw new ConflictException("E01", "La franja seleccionada ya alcanzo el limite maximo de 3 reservas concurrentes");
        }

        List<TurnoDocument> disponiblesDocs = turnoRepository.findByFechaAndHoraInicioAndEstado(fecha, hora, EstadoTurno.DISPONIBLE);
        if (disponiblesDocs.isEmpty()) {
            throw new ConflictException("E01", "No existe ningun turno disponible en la fecha y franja horaria solicitada");
        }

        context.setTurnosDisponibles(disponiblesDocs);
    }
}
