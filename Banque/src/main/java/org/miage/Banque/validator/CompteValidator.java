package org.miage.Banque.validator;

import org.miage.Banque.input.ClientInput;
import org.miage.Banque.input.CompteInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class CompteValidator {

    private Validator validator;

    CompteValidator(Validator validator){
        this.validator = validator;
    }

    public void validate(CompteInput compte) {
        Set<ConstraintViolation<CompteInput>> violations = validator.validate(compte);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
