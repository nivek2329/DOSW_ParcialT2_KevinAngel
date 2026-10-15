package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends MongoRepository<ReservaDocument, String> {
    long countByFechaAndHoraInicioAndEstado(LocalDate fecha, LocalTime horaInicio, EstadoReserva estado);
    List<ReservaDocument> findByClienteEmailOrderByFechaDescHoraInicioDesc(String clienteEmail);
    List<ReservaDocument> findAllByOrderByFechaDescHoraInicioDesc();
}
