package org.miage.Banque.validator;

import org.miage.Banque.input.CarteBancaireInput;
import org.miage.Banque.input.OperationInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class CarteBancaireValidator {

    private Validator validator;

    CarteBancaireValidator(Validator validator){
        this.validator = validator;
    }

    public void validate(CarteBancaireInput cartebancaire) {
        Set<ConstraintViolation<CarteBancaireInput>> violations = validator.validate(cartebancaire);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
