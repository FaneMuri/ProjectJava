package com.example.demo2.domain.validators;



public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}