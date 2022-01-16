package org.miage.Commercant.representation;

import org.miage.Commercant.delegate.CompteDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompteRepresentation {

   @Autowired
   CompteDelegate compteDelegate;

    @RequestMapping(value= "/clients/{clientId}/comptes", method = RequestMethod.GET)
    public String getOneCompteByIdClient (@PathVariable(value = "clientId") String clientId){
        System.out.println("Appel de commercant service");
        return compteDelegate.callTest(clientId);
    }


}
