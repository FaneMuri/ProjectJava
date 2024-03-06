package com.example.demo2.domain.validators;




import com.example.demo2.domain.Utilizator;
import com.example.demo2.domain.validators.ValidationException;
import com.example.demo2.domain.validators.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilizatorValidator implements Validator<Utilizator> {

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        ValidUserName(entity);
    }

    private void ValidUserName(Utilizator entity) {
        String regex = "^[A-Z][a-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entity.getFirstName());
        if (!matcher.find())
            throw new ValidationException("Nume invalid!");
        matcher = pattern.matcher(entity.getLastName());
        if (!matcher.find())
            throw new ValidationException("Prenume invalid!");
        if(entity.getId()<0)
            throw new ValidationException("ID>0");
    }

}

