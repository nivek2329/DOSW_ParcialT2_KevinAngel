package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.TurnoCreateDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.core.models.Turno;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class TurnoMapper {

    public Turno toDomain(TurnoCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        LocalTime horaFin = dto.getHoraInicio() != null ? dto.getHoraInicio().plusHours(1) : null;
        return new Turno(
                null,
                dto.getBarberoId(),
                dto.getFecha(),
                dto.getHoraInicio(),
                horaFin,
                EstadoTurno.DISPONIBLE
        );
    }

    public TurnoResponseDTO toResponseDTO(Turno domain) {
        if (domain == null) {
            return null;
        }
        return new TurnoResponseDTO(
                domain.getId(),
                domain.getBarberoId(),
                domain.getFecha(),
                domain.getHoraInicio(),
                domain.getHoraFin(),
                domain.getEstado() != null ? domain.getEstado().name() : null
        );
    }
}
