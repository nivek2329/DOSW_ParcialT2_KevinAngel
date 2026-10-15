package edu.dosw.parcial.controller.handlers;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.NOT_FOUND);
    }
}
