package edu.dosw.parcial.core.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class Barbero {

    private String id;
    private String nombre;
    private List<Especialidad> especialidades;
    private Disponibilidad disponibilidad;

    public Barbero() {
    }

    public Barbero(String id, String nombre, List<Especialidad> especialidades, Disponibilidad disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidades = especialidades;
        this.disponibilidad = disponibilidad;
    }

    public boolean tieneEspecialidad(Especialidad especialidad) {
        if (especialidades == null) {
            return false;
        }
        return especialidades.contains(especialidad);
    }

    public boolean atiendeEn(DayOfWeek dia, LocalTime hora) {
        if (disponibilidad == null) {
            return false;
        }
        return disponibilidad.estaDisponible(dia, hora);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Disponibilidad disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
