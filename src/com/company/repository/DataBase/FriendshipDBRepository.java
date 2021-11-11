package com.company.repository.DataBase;

import com.company.domain.Friendship;
import com.company.domain.validators.Validator;
import com.company.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class FriendshipDBRepository implements Repository<Long, Friendship> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Long aLong) {
        String sql = String.format("SELECT * from friendships WHERE id = %s", aLong.toString());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()){
            rs.next();
            Long user1ID = rs.getLong("user_id_1");
            Long user2ID = rs.getLong("user_id_2");
            String date = rs.getString("friendship_date");
            Friendship radu = new Friendship(user1ID, user2ID, date);
            radu.setId(aLong);
            return radu;
        } catch (SQLException throwable) {
            //throwable.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> ret = new HashSet<Friendship>();
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet rs = statement.executeQuery()){

            while(rs.next()){
                Long id = rs.getLong("id");
                Long user1ID = rs.getLong("user_id_1");
                Long user2ID = rs.getLong("user_id_2");
                String date = rs.getString("friendship_date");
                Friendship radu = new Friendship(user1ID, user2ID, date);
                radu.setId(id);
                ret.add(radu);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

    @Override
    public Friendship save(Friendship entity) {
        validator.validate(entity);
        String sql1 = String.format("select * from friendships where user_id_1 = %s and user_id_2 = %s and friendship_date = %s", entity.getID1(), entity.getID2(), entity.getDate().toString());
        /*for (Friendship fr : findAll())
            if ((fr.getID1().equals(entity.getID1()) && fr.getID2().equals(entity.getID2())) || (fr.getID1().equals(entity.getID2()) && fr.getID2().equals(entity.getID1())))
                return entity;*/
        String sql = String.format("insert into friendships (id, user_id_1, user_id_2, friendship_date) values (%s, %s, %s, '%s')", entity.getId(), entity.getID1(), entity.getID2(), entity.getDate().toString());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement statement1 = connection.prepareStatement(sql1);
             ResultSet rs = statement1.executeQuery()) {
            if(!rs.next()) {
                statement.executeUpdate();
                return null;
            }
        } catch (SQLException throwable) {
            return entity;
        }
        //return null;
        return null;
    }

    @Override
    public Friendship delete(Long aLong) {
        Friendship radu = findOne(aLong);
        if(radu == null)
            return null;
        String sql = String.format("DELETE from friendships WHERE id = %s", aLong.toString());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return radu;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    public Friendship update(Friendship entity) {
        validator.validate(entity);
        String sql = String.format("UPDATE friendships SET user_id_1 = %s, user_id_2 = %s friendship_date = %s WHERE id = %s", entity.getID1() ,entity.getID2(), entity.getDate(), entity.getId());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.executeUpdate();
            return null;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return entity;
        }
    }
}
