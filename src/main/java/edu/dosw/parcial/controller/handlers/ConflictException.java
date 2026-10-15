package edu.dosw.parcial.controller.handlers;

import org.springframework.http.HttpStatus;

public class ConflictException extends AppException {

    public ConflictException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.CONFLICT);
    }
}
