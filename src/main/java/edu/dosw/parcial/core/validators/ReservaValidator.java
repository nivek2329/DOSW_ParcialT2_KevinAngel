package edu.dosw.parcial.core.validators;

public interface ReservaValidator {
    int getOrder();
    void validate(ReservaValidationContext context);
}
