package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DatosClienteValidator implements ReservaValidator {

    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@gmail\\.com$");

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void validate(ReservaValidationContext context) {
        String nombre = context.getRequest().getClienteNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationBusinessException("E03", "El nombre del cliente no puede ser nulo, vacio o contener unicamente espacios", HttpStatus.BAD_REQUEST);
        }

        String telefono = context.getRequest().getClienteTelefono();
        if (telefono == null || !TELEFONO_PATTERN.matcher(telefono.trim()).matches()) {
            throw new ValidationBusinessException("E07", "El telefono del cliente debe contener exactamente 10 digitos numericos", HttpStatus.BAD_REQUEST);
        }

        String correo = context.getAuthenticatedEmail();
        if (correo == null || correo.trim().isEmpty() || !GMAIL_PATTERN.matcher(correo.trim().toLowerCase()).matches()) {
            throw new ValidationBusinessException("E08", "El correo electronico del cliente es invalido o pertenece a un dominio diferente de @gmail.com", HttpStatus.BAD_REQUEST);
        }
    }
}
