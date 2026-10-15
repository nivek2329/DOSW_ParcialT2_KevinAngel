package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Reserva;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import org.springframework.stereotype.Component;

@Component
public class ReservaEntityMapper {

    public Reserva toDomain(ReservaDocument doc) {
        if (doc == null) {
            return null;
        }
        return new Reserva(
                doc.getId(),
                doc.getTurnoId(),
                doc.getClienteEmail(),
                doc.getClienteNombre(),
                doc.getClienteTelefono(),
                doc.getBarberoId(),
                doc.getFecha(),
                doc.getHoraInicio(),
                doc.getServicios(),
                doc.getPrecioTotal(),
                doc.getEstado(),
                doc.getFechaCreacion()
        );
    }

    public ReservaDocument toDocument(Reserva domain) {
        if (domain == null) {
            return null;
        }
        return new ReservaDocument(
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
                domain.getEstado(),
                domain.getFechaCreacion()
        );
    }
}
