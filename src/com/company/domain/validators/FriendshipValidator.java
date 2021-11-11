package com.company.domain.validators;

import com.company.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String error = "";
        if(entity.getId() == null)
            error += "The friendship has no identifier!\n";
        if(entity.getID1() == null)
            error += "The first user cannot be non existent!\n";
        if(entity.getID2() == null)
            error += "The second user cannot be non existent!\n";
        if(entity.getID1().equals(entity.getID2()))
            error += "A user cannot befriend himself!\n";
        if(error.length() > 0)
            throw new FriendException(error);
    }
}
