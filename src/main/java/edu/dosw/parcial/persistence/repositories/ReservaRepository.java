package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.ReservaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservaRepository extends MongoRepository<ReservaDocument, String> {
    List<ReservaDocument> findByFranjaId(String franjaId);
    List<ReservaDocument> findByClienteEmailOrderByFechaCreacionDesc(String clienteEmail);
}
