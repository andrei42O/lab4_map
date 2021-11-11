package com.company.domain.validators;

import com.company.domain.validators.ValidationException;
/**
 * This is the interface for all the future validators
 */
public interface Validator<T> {
    /**
     * The function validates and entity data
     * @param entity - T
     * @throws ValidationException - if there is incorrect fields
     */
    void validate(T entity) throws ValidationException;
}