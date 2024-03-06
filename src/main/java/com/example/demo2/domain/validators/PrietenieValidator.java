package com.example.demo2.domain.validators;

import com.example.demo2.domain.Prietenie;
import com.example.demo2.domain.Utilizator;
public class PrietenieValidator implements Validator<Prietenie>{
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String err="";
        if (entity.getId().getLeft()<0)
            err+="ID1 Gresit";
        if (entity.getId().getRight()<0)
            err+="ID2 Gresit";
        if( err!="")
            throw new ValidationException(err);
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            throw new ValidationException("Nu poate fi prieten cu el!!!");
    }

}
