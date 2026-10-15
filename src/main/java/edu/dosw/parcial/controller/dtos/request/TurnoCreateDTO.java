package edu.dosw.parcial.controller.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoCreateDTO {

    @NotBlank(message = "El barberoId es obligatorio")
    @Schema(example = "barb-1")
    private String barberoId;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2026-10-25")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    @Schema(type = "string", example = "10:00")
    private LocalTime horaInicio;

    public TurnoCreateDTO() {
    }

    public TurnoCreateDTO(String barberoId, LocalDate fecha, LocalTime horaInicio) {
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
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
}
