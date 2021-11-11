package com.company.domain;

import java.util.Objects;


/**
 * Define a Tuple o generic type entities
 * @param <ID> - tuple first entity type
 * @param <T> - tuple second entity type
 */
public class Tuple<ID, T> {
    private ID e1;
    private T e2;

    public Tuple(ID e1, T e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public ID getLeft() {
        return e1;
    }

    public void setLeft(ID e1) {
        this.e1 = e1;
    }

    public T getRight() {
        return e2;
    }

    public void setRight(T e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "" + e1 + "," + e2;

    }

    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}