package edu.dosw.parcial.core.validators;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ReservaValidationChain {

    private final List<ReservaValidator> validators;

    public ReservaValidationChain(List<ReservaValidator> validators) {
        this.validators = validators.stream()
                .sorted(Comparator.comparingInt(ReservaValidator::getOrder))
                .toList();
    }

    public void executeChain(ReservaValidationContext context) {
        for (ReservaValidator validator : validators) {
            validator.validate(context);
        }
    }
}
