package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.TurnoCreateDTO;
import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.controller.handlers.ResourceNotFoundException;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.controller.mappers.TurnoMapper;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TurnoServiceImpl implements TurnoService {

    private static final Logger log = LoggerFactory.getLogger(TurnoServiceImpl.class);

    private final TurnoRepository turnoRepository;
    private final BarberoRepository barberoRepository;
    private final TurnoMapper turnoMapper;
    private final TurnoEntityMapper turnoEntityMapper;

    public TurnoServiceImpl(TurnoRepository turnoRepository,
                            BarberoRepository barberoRepository,
                            TurnoMapper turnoMapper,
                            TurnoEntityMapper turnoEntityMapper) {
        this.turnoRepository = turnoRepository;
        this.barberoRepository = barberoRepository;
        this.turnoMapper = turnoMapper;
        this.turnoEntityMapper = turnoEntityMapper;
    }

    @Override
    public Turno crearTurno(TurnoCreateDTO request) {
        log.info("Creando turno para barbero {} en fecha {} {}", request.getBarberoId(), request.getFecha(), request.getHoraInicio());

        Optional<BarberoDocument> barberoOpt = barberoRepository.findById(request.getBarberoId());
        if (barberoOpt.isEmpty()) {
            throw new ResourceNotFoundException("E404-BARBERO", "El barbero especificado no existe");
        }

        if (!request.getFecha().isAfter(LocalDate.now())) {
            throw new ValidationBusinessException("E400-FECHA", "La fecha del turno debe ser futura", HttpStatus.BAD_REQUEST);
        }

        BarberoDocument doc = barberoOpt.get();
        Barbero barbero = new Barbero(doc.getId(), doc.getNombre(), doc.getEspecialidades(), doc.getDisponibilidad());

        DayOfWeek dia = request.getFecha().getDayOfWeek();
        if (!barbero.atiendeEn(dia, request.getHoraInicio())) {
            throw new ValidationBusinessException("E422-HORARIO", "La hora u horario solicitado no se encuentra dentro de la disponibilidad del barbero", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<TurnoDocument> duplicado = turnoRepository.findByBarberoIdAndFechaAndHoraInicio(
                request.getBarberoId(), request.getFecha(), request.getHoraInicio());
        if (duplicado.isPresent()) {
            throw new ConflictException("E409-DUPLICADO", "Ya existe un turno publicado para este barbero en la misma fecha y hora");
        }

        Turno turno = turnoMapper.toDomain(request);
        TurnoDocument savedDoc = turnoRepository.save(turnoEntityMapper.toDocument(turno));
        log.info("Turno creado con exito, id: {}", savedDoc.getId());

        return turnoEntityMapper.toDomain(savedDoc);
    }
}
