package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.EstadoTurno;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "turnos")
public class TurnoDocument {

    @Id
    private String id;
    private String barberoId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoTurno estado;

    public TurnoDocument() {
    }

    public TurnoDocument(String id, String barberoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoTurno estado) {
        this.id = id;
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(String barberoId) {
        this.barberoId = barberoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }
}
