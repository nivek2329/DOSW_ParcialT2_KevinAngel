package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.core.models.Reserva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ReservaMapper {

    public Reserva toDomain(ReservaRequestDTO dto, String authenticatedEmail) {
        if (dto == null) {
            return null;
        }
        return new Reserva(
                null,
                null,
                authenticatedEmail,
                dto.getClienteNombre(),
                dto.getClienteTelefono(),
                dto.getBarberoId(),
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getServicios(),
                BigDecimal.ZERO,
                EstadoReserva.CONFIRMADA,
                LocalDateTime.now()
        );
    }

    public ReservaResponseDTO toResponseDTO(Reserva domain) {
        if (domain == null) {
            return null;
        }
        return new ReservaResponseDTO(
                domain.getId(),
                domain.getTurnoId(),
                domain.getClienteEmail(),
                domain.getClienteNombre(),
                domain.getClienteTelefono(),
                domain.getBarberoId(),
                domain.getFecha(),
                domain.getHoraInicio(),
                domain.getServicios(),
                domain.getPrecioTotal(),
                domain.getEstado() != null ? domain.getEstado().name() : null,
                domain.getFechaCreacion()
        );
    }
}
