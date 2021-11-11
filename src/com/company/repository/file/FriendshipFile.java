package com.company.repository.file;

import com.company.domain.Friendship;
import com.company.domain.User;
import com.company.domain.validators.Validator;

import java.util.Arrays;
import java.util.List;

public class FriendshipFile extends  AbstractFileRepository<Long, Friendship>{
    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return  entity.getId().toString() + ";" +
                entity.getID1() + ";" +
                entity.getID2();
    }

    @Override
    protected Friendship extractEntity(List<String> args) {
        Friendship ret = new Friendship(Long.parseLong(args.get(1)), Long.parseLong(args.get(2)));
        ret.setId(Long.parseLong(args.get(0)));
        return ret;
    }

}
