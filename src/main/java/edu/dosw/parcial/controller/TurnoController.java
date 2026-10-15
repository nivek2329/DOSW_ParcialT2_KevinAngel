package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.TurnoCreateDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.controller.mappers.TurnoMapper;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/turnos")
@Tag(name = "Turnos", description = "Endpoints para gestion y creacion de franjas horarias de barberia")
public class TurnoController {

    private final TurnoService turnoService;
    private final TurnoMapper turnoMapper;

    public TurnoController(TurnoService turnoService, TurnoMapper turnoMapper) {
        this.turnoService = turnoService;
        this.turnoMapper = turnoMapper;
    }

    @PostMapping
    @Operation(summary = "Crear turno (Solo ADMIN)", description = "Publica una nueva franja horaria de 1 hora para un barbero disponible")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos o fecha no futura"),
            @ApiResponse(responseCode = "404", description = "Barbero no encontrado"),
            @ApiResponse(responseCode = "409", description = "Turno duplicado para el mismo barbero, fecha y hora"),
            @ApiResponse(responseCode = "422", description = "Horario no compatible con la disponibilidad del barbero")
    })
    public ResponseEntity<TurnoResponseDTO> crearTurno(@Valid @RequestBody TurnoCreateDTO request) {
        Turno nuevoTurno = turnoService.crearTurno(request);
        TurnoResponseDTO response = turnoMapper.toResponseDTO(nuevoTurno);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
