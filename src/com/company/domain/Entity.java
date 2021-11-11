package com.company.domain;

import java.io.Serializable;

/**
 * Define an entity generic class
 * @param <ID> - type of id
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}