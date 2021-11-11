package com.company.repository.InMemoryRepository;

import com.company.domain.Entity;
import com.company.domain.validators.FriendException;
import com.company.domain.validators.Validator;
import com.company.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        for (E value : entities.values())
            if(value.equals(entity))
                return entity;
        entities.put(entity.getId(),entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        if(entities.containsKey(id)) {
            E entity = entities.get(id);
            entities.remove(id);
            return entity;
        }
        return null;
    }

    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

/*    public void addFriendship(E entity1, E entity2){
        if(entity1 == null || entity2 == null)
            throw new IllegalArgumentException("Both arguments must not be null!\n");
        if(friendships.containsKey(entity1.getId())) {
            if (friendships
                    .get(entity1.getId())
                    .contains(entity2.getId())) {
                throw new FriendException("Already Friends!\n");
            } else
                friendships.get(entity1.getId()).add(entity2.getId());
        }


    }*/
}
