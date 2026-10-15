package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.handlers.ResourceNotFoundException;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Especialidad;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BarberoDisponibleValidator implements ReservaValidator {

    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final TurnoEntityMapper turnoEntityMapper;

    public BarberoDisponibleValidator(BarberoRepository barberoRepository,
                                      ServicioRepository servicioRepository,
                                      TurnoEntityMapper turnoEntityMapper) {
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.turnoEntityMapper = turnoEntityMapper;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void validate(ReservaValidationContext context) {
        List<TurnoDocument> disponibles = context.getTurnosDisponibles();
        if (disponibles == null || disponibles.isEmpty()) {
            throw new ValidationBusinessException("E100", "No existen turnos disponibles para asignar en esta franja", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        LocalDate fecha = context.getRequest().getFecha();
        LocalTime hora = context.getRequest().getHoraInicio();
        DayOfWeek dia = fecha.getDayOfWeek();

        List<Especialidad> requeridas = obtenerEspecialidadesRequeridas(context.getRequest().getServicios());
        String preferidoBarberoId = context.getRequest().getBarberoId();

        if (preferidoBarberoId != null && !preferidoBarberoId.trim().isEmpty()) {
            validarBarberoPreferido(context, disponibles, preferidoBarberoId.trim(), dia, hora, requeridas);
        } else {
            asignarBarberoAutomatico(context, disponibles, dia, hora, requeridas);
        }
    }

    private void validarBarberoPreferido(ReservaValidationContext context, List<TurnoDocument> disponibles,
                                         String barberoId, DayOfWeek dia, LocalTime hora, List<Especialidad> requeridas) {
        Optional<BarberoDocument> barberoDocOpt = barberoRepository.findById(barberoId);
        if (barberoDocOpt.isEmpty()) {
            throw new ResourceNotFoundException("E100", "El barbero solicitado no existe en el sistema: " + barberoId);
        }

        TurnoDocument turnoDoc = null;
        for (TurnoDocument doc : disponibles) {
            if (barberoId.equalsIgnoreCase(doc.getBarberoId())) {
                turnoDoc = doc;
                break;
            }
        }

        if (turnoDoc == null) {
            throw new ValidationBusinessException("E100", "El barbero solicitado no se encuentra disponible en esa franja", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        BarberoDocument doc = barberoDocOpt.get();
        Barbero barbero = new Barbero(doc.getId(), doc.getNombre(), doc.getEspecialidades(), doc.getDisponibilidad());

        if (!barbero.atiendeEn(dia, hora)) {
            throw new ValidationBusinessException("E100", "El barbero seleccionado no atiende en el horario solicitado", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        for (Especialidad req : requeridas) {
            if (!barbero.tieneEspecialidad(req)) {
                throw new ValidationBusinessException("E02", "El barbero seleccionado no tiene la especialidad requerida para alguno de los servicios solicitados", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        context.setTurnoSeleccionado(turnoEntityMapper.toDomain(turnoDoc));
        context.setBarberoAsignado(barbero);
    }

    private void asignarBarberoAutomatico(ReservaValidationContext context, List<TurnoDocument> disponibles,
                                          DayOfWeek dia, LocalTime hora, List<Especialidad> requeridas) {
        TurnoDocument turnoElegido = null;
        Barbero barberoElegido = null;

        for (TurnoDocument doc : disponibles) {
            Optional<BarberoDocument> bOpt = barberoRepository.findById(doc.getBarberoId());
            if (bOpt.isEmpty()) {
                continue;
            }

            BarberoDocument bDoc = bOpt.get();
            Barbero barbero = new Barbero(bDoc.getId(), bDoc.getNombre(), bDoc.getEspecialidades(), bDoc.getDisponibilidad());

            if (!barbero.atiendeEn(dia, hora)) {
                continue;
            }

            boolean cumpleEspecialidades = true;
            for (Especialidad req : requeridas) {
                if (!barbero.tieneEspecialidad(req)) {
                    cumpleEspecialidades = false;
                    break;
                }
            }

            if (cumpleEspecialidades) {
                turnoElegido = doc;
                barberoElegido = barbero;
                break;
            }
        }

        if (turnoElegido == null || barberoElegido == null) {
            throw new ValidationBusinessException("E100", "Ningun barbero con la especialidad requerida esta libre en esa franja", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Turno turnoDomain = turnoEntityMapper.toDomain(turnoElegido);
        context.setTurnoSeleccionado(turnoDomain);
        context.setBarberoAsignado(barberoElegido);
    }

    private List<Especialidad> obtenerEspecialidadesRequeridas(List<String> codigosServicios) {
        List<Especialidad> lista = new ArrayList<>();
        if (codigosServicios == null) {
            return lista;
        }

        for (String cod : codigosServicios) {
            Optional<ServicioDocument> sOpt = servicioRepository.findByCodigo(cod);
            if (sOpt.isPresent() && sOpt.get().getEspecialidad() != null) {
                lista.add(sOpt.get().getEspecialidad());
            }
        }
        return lista;
    }
}
