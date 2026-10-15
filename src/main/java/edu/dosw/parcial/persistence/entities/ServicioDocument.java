package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.Especialidad;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "servicios")
public class ServicioDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nombre;
    private BigDecimal precio;
    private Especialidad especialidad;
    private boolean activo;

    public ServicioDocument() {
    }

    public ServicioDocument(String id, String codigo, String nombre, BigDecimal precio, Especialidad especialidad, boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.especialidad = especialidad;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
