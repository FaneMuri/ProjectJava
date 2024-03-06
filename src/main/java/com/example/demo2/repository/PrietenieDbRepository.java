package com.example.demo2.repository;





import com.example.demo2.domain.*;

import java.net.URI;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDbRepository implements Repository< Tuple<Long,Long> , Prietenie> {

    private final String url;
    private final String user;
    private final String password;


    public PrietenieDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

    }


    @Override
    public Optional<Prietenie> findOne(Tuple<Long,Long> longID) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE user1_id=? AND user2_id=?")) {
            statement.setLong(1, longID.getLeft());
            statement.setLong(2, longID.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime friends_from = resultSet.getTimestamp("friends_from").toLocalDateTime();
                CererePrietenie status  = CererePrietenie.valueOf(resultSet.getString("friend_request_status"));
                Prietenie f = new Prietenie();
                f.setId(new Tuple<>(user1_id,user2_id));
                f.setDate(friends_from);
                f.setCerere(status);
                return Optional.of(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
               // Long id = resultSet.getLong("id");
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime friends_from = resultSet.getTimestamp("friends_from").toLocalDateTime();
                CererePrietenie status = CererePrietenie.valueOf(resultSet.getString("friend_request_status"));
                Prietenie f = new Prietenie();
                f.setId(new Tuple<>(user1_id,user2_id));
                f.setDate(friends_from);
                f.setCerere(status);
                friendships.add(f);
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO friendships(user1_id,user2_id,friends_from,friend_request_status) VALUES (?,?,?,?)"))
        {
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4,entity.getCerere().toString());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long,Long> longID) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM friendships WHERE user1_id=? AND user2_id=?"))
        {
            statement.setLong(1, longID.getLeft());
            statement.setLong(2, longID.getRight());
            Optional<Prietenie> cv = findOne(longID);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE friendships SET user1_id = ?, user2_id = ?, friends_from = ?, friend_request_status = ? WHERE user1_id = ? and user2_id = ?"))
        {
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            //statement.setLong(4,entity.getId());
            statement.setString(4, entity.getCerere().toString());
            statement.setLong(5,entity.getId().getLeft());
            statement.setLong(6,entity.getId().getRight());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}