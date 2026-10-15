package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface TurnoRepository extends MongoRepository<TurnoDocument, String> {
    Optional<TurnoDocument> findByBarberoIdAndFechaAndHoraInicio(String barberoId, LocalDate fecha, LocalTime horaInicio);
}
