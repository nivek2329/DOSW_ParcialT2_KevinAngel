package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.handlers.ConflictException;
import edu.dosw.parcial.controller.mappers.ReservaMapper;
import edu.dosw.parcial.core.models.EstadoReserva;
import edu.dosw.parcial.core.models.EstadoTurno;
import edu.dosw.parcial.core.models.Reserva;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.validators.ReservaValidationChain;
import edu.dosw.parcial.core.validators.ReservaValidationContext;
import edu.dosw.parcial.persistence.entities.FranjaQuotaDocument;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.ReservaEntityMapper;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository reservaRepository;
    private final MongoTemplate mongoTemplate;
    private final ReservaValidationChain validationChain;
    private final ReservaMapper reservaMapper;
    private final ReservaEntityMapper reservaEntityMapper;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              MongoTemplate mongoTemplate,
                              ReservaValidationChain validationChain,
                              ReservaMapper reservaMapper,
                              ReservaEntityMapper reservaEntityMapper) {
        this.reservaRepository = reservaRepository;
        this.mongoTemplate = mongoTemplate;
        this.validationChain = validationChain;
        this.reservaMapper = reservaMapper;
        this.reservaEntityMapper = reservaEntityMapper;
    }

    @Override
    @Transactional
    public Reserva pedirTurno(ReservaRequestDTO request, String authenticatedEmail) {
        log.info("Iniciando solicitud de reserva para cliente {} en fecha {} {}", authenticatedEmail, request.getFecha(), request.getHoraInicio());

        ReservaValidationContext context = new ReservaValidationContext(request, authenticatedEmail);
        validationChain.executeChain(context);

        String franjaId = request.getFecha().toString() + "_" + request.getHoraInicio().toString();

        asegurarInicializacionCuota(franjaId, request);

        Query quotaQuery = new Query(Criteria.where("_id").is(franjaId).and("conteoReservas").lt(3));
        Update quotaUpdate = new Update().inc("conteoReservas", 1);

        FranjaQuotaDocument quotaDoc = mongoTemplate.findAndModify(
                quotaQuery,
                quotaUpdate,
                FindAndModifyOptions.options().returnNew(true),
                FranjaQuotaDocument.class
        );

        if (quotaDoc == null) {
            log.warn("Cuota maxima de 3 reservas alcanzada concurrentemente para la franja {}", franjaId);
            throw new ConflictException("E01", "La franja seleccionada ya alcanzo el limite maximo de 3 reservas concurrentes");
        }

        Turno turno = context.getTurnoSeleccionado();

        Query occupyQuery = new Query(Criteria.where("_id").is(turno.getId()).and("estado").is(EstadoTurno.DISPONIBLE));
        Update occupyUpdate = new Update().set("estado", EstadoTurno.OCUPADO);
        TurnoDocument turnoOcupadoDoc = mongoTemplate.findAndModify(
                occupyQuery, occupyUpdate, FindAndModifyOptions.options().returnNew(true), TurnoDocument.class);

        if (turnoOcupadoDoc == null) {
            log.warn("Conflicto concurrente multi-instancia: El turno {} ya fue tomado", turno.getId());
            revertirCuota(franjaId);
            throw new ConflictException("E01", "El turno seleccionado fue ocupado concurrentemente por otra solicitud");
        }

        try {
            Reserva reserva = reservaMapper.toDomain(request, authenticatedEmail);
            reserva.setTurnoId(turno.getId());
            reserva.setBarberoId(context.getBarberoAsignado().getId());
            reserva.calcularTotal(context.getCatalogoServicios());

            ReservaDocument reservaDoc = reservaEntityMapper.toDocument(reserva);
            ReservaDocument savedReservaDoc = reservaRepository.save(reservaDoc);

            log.info("Reserva {} confirmada con exito para cliente {}", savedReservaDoc.getId(), authenticatedEmail);
            return reservaEntityMapper.toDomain(savedReservaDoc);

        } catch (Exception ex) {
            log.error("Fallo inesperado al persistir la reserva. Revertiendo cuota y turno...", ex);
            revertirCuota(franjaId);
            revertirTurno(turno.getId());
            throw ex;
        }
    }

    private void asegurarInicializacionCuota(String franjaId, ReservaRequestDTO request) {
        try {
            Query existQuery = new Query(Criteria.where("_id").is(franjaId));
            long reservasPrevias = reservaRepository.countByFechaAndHoraInicioAndEstado(
                    request.getFecha(), request.getHoraInicio(), EstadoReserva.CONFIRMADA);

            Update initUpdate = new Update()
                    .setOnInsert("fecha", request.getFecha())
                    .setOnInsert("horaInicio", request.getHoraInicio())
                    .setOnInsert("conteoReservas", (int) reservasPrevias);

            mongoTemplate.upsert(existQuery, initUpdate, FranjaQuotaDocument.class);
        } catch (DuplicateKeyException ex) {
            log.debug("El contador de cuota ya fue inicializado por otra instancia concurrente para franja: {}", franjaId);
        }
    }

    private void revertirCuota(String franjaId) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(franjaId)),
                new Update().inc("conteoReservas", -1),
                FranjaQuotaDocument.class
        );
    }

    private void revertirTurno(String turnoId) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(turnoId)),
                new Update().set("estado", EstadoTurno.DISPONIBLE),
                TurnoDocument.class
        );
    }

    @Override
    public List<Reserva> consultarMisReservas(String authenticatedEmail) {
        log.info("Consultando reservas para cliente autenticado: {}", authenticatedEmail);
        List<ReservaDocument> docs = reservaRepository.findByClienteEmailOrderByFechaDescHoraInicioDesc(authenticatedEmail);
        List<Reserva> result = new ArrayList<>();
        for (ReservaDocument doc : docs) {
            result.add(reservaEntityMapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public List<Reserva> consultarTodasLasReservas() {
        log.info("Consultando todas las reservas del sistema (Administrador)");
        List<ReservaDocument> docs = reservaRepository.findAllByOrderByFechaDescHoraInicioDesc();
        List<Reserva> result = new ArrayList<>();
        for (ReservaDocument doc : docs) {
            result.add(reservaEntityMapper.toDomain(doc));
        }
        return result;
    }
}
