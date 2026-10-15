package edu.dosw.parcial.controller.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaRequestDTO {

    private String clienteNombre;
    private String clienteTelefono;
    private String clienteEmail;
    private String barberoId;

    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "2026-10-25")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    @io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "10:00")
    private LocalTime horaInicio;

    @NotEmpty(message = "Debe solicitar al menos un servicio")
    private List<String> servicios;

    public ReservaRequestDTO() {
    }

    public ReservaRequestDTO(String clienteNombre, String clienteTelefono, String clienteEmail,
                             String barberoId, LocalDate fecha, LocalTime horaInicio, List<String> servicios) {
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
        this.clienteEmail = clienteEmail;
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.servicios = servicios;
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

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
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
}
