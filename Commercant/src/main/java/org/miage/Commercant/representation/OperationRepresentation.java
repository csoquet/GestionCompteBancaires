package org.miage.Commercant.representation;

import org.miage.Commercant.delegate.OperationDelegate;
import org.miage.Commercant.entity.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OperationRepresentation {

    @Autowired
    OperationDelegate operationDelegate;

   @RequestMapping(value= "/clients/{clientId}/comptes/{compteId}/operations", method = RequestMethod.POST)
    public String postOperation (@PathVariable(value = "clientId") String clientId, @PathVariable(value = "compteId") String compteId, @RequestBody Operation operation){
        System.out.println("Appel de commercant service");
        return operationDelegate.callPostOperation(clientId, compteId, operation);
    }
}
