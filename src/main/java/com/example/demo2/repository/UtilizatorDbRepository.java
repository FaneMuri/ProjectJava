package com.example.demo2.repository;



import com.example.demo2.domain.Utilizator;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorDbRepository implements Repository<Long, Utilizator>{

    private String url;
    private String username;
    private String password;

    public UtilizatorDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {

        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM users WHERE id = ? ");

        ){
            preparedStatement.setLong(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                Integer Id = resultSet.getInt("id");

                Utilizator user = new Utilizator(first_name,last_name);
                user.setId(Id.longValue());
                return Optional.ofNullable(user);
            }

            return Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Iterable<Utilizator> findAll() {
        List<Utilizator> userList = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM users");

        ){
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");


                Utilizator user = new Utilizator(first_name,last_name);
                user.setId(id);

                userList.add(user);
            }
            return userList;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO users (first_name,last_name,id) VALUES (?,?,?)");

        ){
            preparedStatement.setString(1,entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());

            preparedStatement.setInt(3,entity.getId().intValue());
            int affectedRows=preparedStatement.executeUpdate();
            /*
            if(affectedRows == 0)
            {
                return Optional.empty();
            }
            else return Optional.ofNullable(entity);
            */
            return affectedRows == 0 ? Optional.empty() : Optional.ofNullable(entity);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        Optional<Utilizator> user = this.findOne(id);
        int affectedRows = 0;
        if(user.isPresent()) {
            try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?");

            ) {
                preparedStatement.setLong(1, id);
                affectedRows = preparedStatement.executeUpdate();
                return affectedRows == 0 ? Optional.empty() : user;


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, id = ? WHERE id = ?");

        ){
            preparedStatement.setString(1,entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());
            preparedStatement.setInt(3,entity.getId().intValue());
            preparedStatement.setInt(4,entity.getId().intValue());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}

