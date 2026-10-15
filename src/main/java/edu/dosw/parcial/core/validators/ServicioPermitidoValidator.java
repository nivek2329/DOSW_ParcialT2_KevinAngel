package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.controller.handlers.ResourceNotFoundException;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServicioPermitidoValidator implements ReservaValidator {

    private final ServicioRepository servicioRepository;

    public ServicioPermitidoValidator(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public void validate(ReservaValidationContext context) {
        List<String> codigos = context.getRequest().getServicios();
        if (codigos == null || codigos.isEmpty()) {
            throw new ValidationBusinessException("E09", "Debe solicitar al menos un servicio del catalogo", HttpStatus.BAD_REQUEST);
        }

        List<ServicioDocument> activosDocs = servicioRepository.findByActivoTrue();
        List<Servicio> catalogoActivo = new ArrayList<>();
        for (ServicioDocument doc : activosDocs) {
            catalogoActivo.add(new Servicio(doc.getCodigo(), doc.getNombre(), doc.getPrecio(), doc.getEspecialidad(), doc.isActivo()));
        }

        for (String cod : codigos) {
            boolean existe = false;
            for (Servicio serv : catalogoActivo) {
                if (serv.getCodigo().equalsIgnoreCase(cod)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                throw new ResourceNotFoundException("E09", "Se solicita un identificador de servicio que no existe en el catalogo activo: " + cod);
            }
        }

        context.setCatalogoServicios(catalogoActivo);
    }
}
