package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.BarberoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BarberoRepository extends MongoRepository<BarberoDocument, String> {
}
