package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.controller.mappers.ReservaMapper;
import edu.dosw.parcial.core.models.Reserva;
import edu.dosw.parcial.core.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@Tag(name = "Reservas", description = "Endpoints para solicitud y consulta de reservas de turnos")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapper reservaMapper;

    public ReservaController(ReservaService reservaService, ReservaMapper reservaMapper) {
        this.reservaService = reservaService;
        this.reservaMapper = reservaMapper;
    }

    @PostMapping
    @Operation(summary = "Pedir un turno (Solo CLIENTE)", description = "Ejecuta la cadena de validaciones en orden estricto para reservar un turno")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de cliente invalidos (nombre, telefono o correo @gmail.com)"),
            @ApiResponse(responseCode = "404", description = "Servicio o barbero solicitado no encontrado en el sistema"),
            @ApiResponse(responseCode = "409", description = "Franja horaria llena (limite de 3 clientes superado) o turno no disponible"),
            @ApiResponse(responseCode = "422", description = "Especialidad incompatible o barbero no disponible en horario solicitado")
    })
    public ResponseEntity<ReservaResponseDTO> pedirTurno(@Valid @RequestBody ReservaRequestDTO request,
                                                         Authentication authentication) {
        String authenticatedEmail = authentication != null ? authentication.getName() : request.getCliente().getEmail();
        Reserva reserva = reservaService.pedirTurno(request, authenticatedEmail);
        ReservaResponseDTO response = reservaMapper.toResponseDTO(reserva);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Consultar mis reservas (Solo CLIENTE)", description = "Retorna las reservas del cliente autenticado extraido del token o contexto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta exitosa de reservas del cliente")
    })
    public ResponseEntity<List<ReservaResponseDTO>> consultarMisReservas(Principal principal, Authentication authentication) {
        String email = principal != null ? principal.getName() : (authentication != null ? authentication.getName() : "");
        List<Reserva> reservas = reservaService.consultarMisReservas(email);
        List<ReservaResponseDTO> response = new ArrayList<>();
        for (Reserva r : reservas) {
            response.add(reservaMapper.toResponseDTO(r));
        }
        return ResponseEntity.ok(response);
    }
}
