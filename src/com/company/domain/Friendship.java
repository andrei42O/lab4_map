package com.company.domain;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Friendship extends Entity<Long>{
    Long ID1;
    Long ID2;
    LocalDate date;

    public Friendship(Long ID1, Long ID2) {
        this.ID1 = ID1;
        this.ID2 = ID2;
        this.date = LocalDate.now();
        //System.out.println(ID1 + " s-a imprietenit cu " + ID2 + " la data: " + this.date);
    }

    public Friendship(Long ID1, Long ID2, String date){
        this.ID1 = ID1;
        this.ID2 = ID2;
        this.date = LocalDate.parse(date);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getID1() {
        return ID1;
    }

    public void setID1(Long ID1) {
        this.ID1 = ID1;
    }

    public Long getID2() {
        return ID2;
    }

    public void setID2(Long ID2) {
        this.ID2 = ID2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getID2(), this.getID2());
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (!(obj instanceof Friendship)) return false;
        Friendship temp = (Friendship) obj;
        return getID1().equals(temp.getID1()) &&
                getID2().equals(temp.getID2());
     }

    @Override
    public String toString() {
        return String.format("%s s-a imprietenit cu %s in data %s", getID1(), getID2(), getDate());
    }
}
