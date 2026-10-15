package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.TurnoDocument;

import java.util.List;

public class ReservaValidationContext {

    private final ReservaRequestDTO request;
    private final String authenticatedEmail;
    private List<TurnoDocument> turnosDisponibles;
    private Turno turnoSeleccionado;
    private Barbero barberoAsignado;
    private List<Servicio> catalogoServicios;

    public ReservaValidationContext(ReservaRequestDTO request, String authenticatedEmail) {
        this.request = request;
        this.authenticatedEmail = authenticatedEmail;
    }

    public ReservaRequestDTO getRequest() {
        return request;
    }

    public String getAuthenticatedEmail() {
        return authenticatedEmail;
    }

    public List<TurnoDocument> getTurnosDisponibles() {
        return turnosDisponibles;
    }

    public void setTurnosDisponibles(List<TurnoDocument> turnosDisponibles) {
        this.turnosDisponibles = turnosDisponibles;
    }

    public Turno getTurnoSeleccionado() {
        return turnoSeleccionado;
    }

    public void setTurnoSeleccionado(Turno turnoSeleccionado) {
        this.turnoSeleccionado = turnoSeleccionado;
    }

    public Barbero getBarberoAsignado() {
        return barberoAsignado;
    }

    public void setBarberoAsignado(Barbero barberoAsignado) {
        this.barberoAsignado = barberoAsignado;
    }

    public List<Servicio> getCatalogoServicios() {
        return catalogoServicios;
    }

    public void setCatalogoServicios(List<Servicio> catalogoServicios) {
        this.catalogoServicios = catalogoServicios;
    }
}
