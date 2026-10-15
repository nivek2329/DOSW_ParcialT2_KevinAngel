package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.EstadoReserva;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Document(collection = "reservas")
public class ReservaDocument {

    @Id
    private String id;
    private String turnoId;
    private String clienteEmail;
    private String clienteNombre;
    private String clienteTelefono;
    private String barberoId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private List<String> servicios;
    private BigDecimal precioTotal;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;

    public ReservaDocument() {
    }

    public ReservaDocument(String id, String turnoId, String clienteEmail, String clienteNombre, String clienteTelefono,
                           String barberoId, LocalDate fecha, LocalTime horaInicio, List<String> servicios,
                           BigDecimal precioTotal, EstadoReserva estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.turnoId = turnoId;
        this.clienteEmail = clienteEmail;
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.servicios = servicios;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(String turnoId) {
        this.turnoId = turnoId;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
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

    public List<String> getServicios() {
        return servicios;
    }

    public void setServicios(List<String> servicios) {
        this.servicios = servicios;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
