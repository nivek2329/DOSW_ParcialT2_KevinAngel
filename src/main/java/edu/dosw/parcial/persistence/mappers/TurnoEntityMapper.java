package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.stereotype.Component;

@Component
public class TurnoEntityMapper {

    public Turno toDomain(TurnoDocument doc) {
        if (doc == null) {
            return null;
        }
        return new Turno(
                doc.getId(),
                doc.getBarberoId(),
                doc.getFecha(),
                doc.getHoraInicio(),
                doc.getHoraFin(),
                doc.getEstado()
        );
    }

    public TurnoDocument toDocument(Turno domain) {
        if (domain == null) {
            return null;
        }
        return new TurnoDocument(
                domain.getId(),
                domain.getBarberoId(),
                domain.getFecha(),
                domain.getHoraInicio(),
                domain.getHoraFin(),
                domain.getEstado()
        );
    }
}
