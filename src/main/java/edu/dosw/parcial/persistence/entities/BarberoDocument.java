package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.Disponibilidad;
import edu.dosw.parcial.core.models.Especialidad;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "barberos")
public class BarberoDocument {

    @Id
    private String id;
    private String nombre;
    private List<Especialidad> especialidades;
    private Disponibilidad disponibilidad;

    public BarberoDocument() {
    }

    public BarberoDocument(String id, String nombre, List<Especialidad> especialidades, Disponibilidad disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidades = especialidades;
        this.disponibilidad = disponibilidad;
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
