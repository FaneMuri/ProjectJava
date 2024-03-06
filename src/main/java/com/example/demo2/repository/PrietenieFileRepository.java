package com.example.demo2.repository;



import com.example.demo2.domain.Prietenie;
import com.example.demo2.domain.Tuple;
import com.example.demo2.domain.Utilizator;
import com.example.demo2.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class PrietenieFileRepository extends AbstractFileRepository<Tuple<Long,Long>, Prietenie>{
    public PrietenieFileRepository(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    protected Prietenie extractEntity(List<String> attributes) {
        Prietenie prietenie = new Prietenie();
        prietenie.setId(new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))) );
        prietenie.setDate(LocalDateTime.parse(attributes.get(2)));

        return prietenie;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }


}
