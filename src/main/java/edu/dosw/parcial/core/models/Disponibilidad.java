package edu.dosw.parcial.core.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public class Disponibilidad {

    private Set<DayOfWeek> dias;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Disponibilidad() {
    }

    public Disponibilidad(Set<DayOfWeek> dias, LocalTime horaInicio, LocalTime horaFin) {
        this.dias = dias;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public boolean estaDisponible(DayOfWeek dia, LocalTime hora) {
        if (dias == null || !dias.contains(dia)) {
            return false;
        }
        if (hora == null || horaInicio == null || horaFin == null) {
            return false;
        }
        return !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    public Set<DayOfWeek> getDias() {
        return dias;
    }

    public void setDias(Set<DayOfWeek> dias) {
        this.dias = dias;
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
}
