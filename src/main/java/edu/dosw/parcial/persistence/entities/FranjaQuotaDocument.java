package edu.dosw.parcial.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "franjas_cuotas")
public class FranjaQuotaDocument {

    @Id
    private String id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int conteoReservas;

    public FranjaQuotaDocument() {
    }

    public FranjaQuotaDocument(String id, LocalDate fecha, LocalTime horaInicio, int conteoReservas) {
        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.conteoReservas = conteoReservas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getConteoReservas() {
        return conteoReservas;
    }

    public void setConteoReservas(int conteoReservas) {
        this.conteoReservas = conteoReservas;
    }
}
