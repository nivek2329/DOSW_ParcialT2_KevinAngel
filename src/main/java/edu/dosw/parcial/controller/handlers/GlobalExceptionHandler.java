package edu.dosw.parcial.controller.handlers;

import edu.dosw.parcial.controller.dtos.response.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppException(AppException ex) {
        log.warn("Excepcion de negocio [{}] : {}", ex.getCodigo(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getCodigo(), ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.warn("Conflicto de concurrencia por clave duplicada: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO("E01", "Conflicto de concurrencia al procesar la reserva", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Error de validacion en datos de entrada";
        log.warn("Error de validacion de entrada: {}", message);
        ErrorResponseDTO error = new ErrorResponseDTO("E00-BAD-REQUEST", message, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO("E403-FORBIDDEN", "No tiene los permisos requeridos para acceder a este recurso", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("Error no controlado en el servidor: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("E500-INTERNAL-ERROR", ex.getMessage() != null ? ex.getMessage() : "Ocurrio un error inesperado al procesar la solicitud", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
