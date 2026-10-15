package edu.dosw.parcial.core.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {

    private String id;
    private String barberoId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoTurno estado;

    public Turno() {
    }

    public Turno(String id, String barberoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoTurno estado) {
        this.id = id;
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public boolean estaDisponible() {
        return EstadoTurno.DISPONIBLE.equals(this.estado);
    }

    public void ocupar() {
        this.estado = EstadoTurno.OCUPADO;
    }

    public void liberar() {
        this.estado = EstadoTurno.DISPONIBLE;
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
