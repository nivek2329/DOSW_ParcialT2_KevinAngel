package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.ServicioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends MongoRepository<ServicioDocument, String> {
    Optional<ServicioDocument> findByCodigo(String codigo);
    List<ServicioDocument> findByActivoTrue();
}
