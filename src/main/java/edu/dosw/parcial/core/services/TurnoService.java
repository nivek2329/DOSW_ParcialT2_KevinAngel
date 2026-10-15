package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.TurnoCreateDTO;
import edu.dosw.parcial.core.models.Turno;

public interface TurnoService {
    Turno crearTurno(TurnoCreateDTO dto);
}
