package com.company.repository.file;

import com.company.domain.User;
import com.company.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User> {

    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        User frateleUti = new User(attributes.get(1), attributes.get(2));
        frateleUti.setId(Long.parseLong(attributes.get(0)));
        return frateleUti;
    }

    @Override
    protected String createEntityAsString(User entity) {
        String ret = "";
        ret += entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return ret;
    }



}
