package com.company.domain.validators;

import com.company.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String err = "";

        // id check
        if(entity.getId() == null)
            err += "The id cannot be null!\n";

        // First name check
        if(entity.getFirstName().length() <= 0)
            err += "The first name cannot be empty!\n";
        // Second name check
        if(entity.getLastName().length() <= 0)
            err += "The last name cannot be empty!\n";

        // Full name check
        if(!entity.getFirstName().matches("^[A-Z][a-z]+(-[A-Z][a-z]+)?$"))
            err += "The first name must start with capital letter and only have letters!\n";
        if(!entity.getLastName().matches("^[A-Z][a-z]+$"))
            err += "The last name must start with capital letter and only have letters!\n";

        if(err.length() != 0)
            throw new ValidationException(err);

    }
}
