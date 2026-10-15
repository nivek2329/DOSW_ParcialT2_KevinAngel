package edu.dosw.parcial.controller.handlers;

import org.springframework.http.HttpStatus;

public class ValidationBusinessException extends AppException {

    public ValidationBusinessException(String codigo, String mensaje, HttpStatus status) {
        super(codigo, mensaje, status);
    }
}
