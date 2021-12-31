package org.miage.Banque.validator;

import org.miage.Banque.input.ClientInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ClientValidator {

    private Validator validator;

    ClientValidator(Validator validator){
        this.validator = validator;
    }

    public void validate(ClientInput client) {
        Set<ConstraintViolation<ClientInput>> violations = validator.validate(client);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
