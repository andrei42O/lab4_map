package com.company.repository.DataBase;

import com.company.domain.User;
import com.company.domain.validators.Validator;
import com.company.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long aLong) {
        String sql = String.format("SELECT * from users WHERE id = %s", aLong.toString());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()){
            rs.next();
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            User radu = new User(firstName, lastName);
            radu.setId(aLong);
            return radu;
        } catch (SQLException throwable) {
            //throwable.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<User> findAll() {
        List<User> ret = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User temp = new User(firstName, lastName);
                temp.setId(id);
                ret.add(temp);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

    @Override
    public User save(User entity) {
        validator.validate(entity);
        String sql = String.format("insert into users (id, first_name, last_name) values (%s, '%s', '%s')", entity.getId(), entity.getFirstName(), entity.getLastName());
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException throwable) {
            return entity;
        }
        return null;
    }

    @Override
    public User delete(Long aLong) {
        User radu = findOne(aLong);
        if(radu == null)
            return null;
        String sql = String.format("DELETE from users WHERE id = %s", aLong.toString());
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
    public User update(User entity) {
        validator.validate(entity);
        String sql = String.format("UPDATE users SET first_name = '%s', last_name = '%s' WHERE id = %s", entity.getFirstName(),entity.getLastName(), entity.getId().toString());
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
