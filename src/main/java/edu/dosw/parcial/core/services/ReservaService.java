package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.core.models.Reserva;

import java.util.List;

public interface ReservaService {
    Reserva pedirTurno(ReservaRequestDTO request, String authenticatedEmail);
    List<Reserva> consultarMisReservas(String authenticatedEmail);
    List<Reserva> consultarTodasLasReservas();
}
