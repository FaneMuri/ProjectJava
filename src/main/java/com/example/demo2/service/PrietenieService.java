package com.example.demo2.service;



import com.example.demo2.domain.CererePrietenie;
import com.example.demo2.domain.Prietenie;
import com.example.demo2.domain.Tuple;
import com.example.demo2.domain.Utilizator;
import com.example.demo2.domain.validators.PrietenieValidator;
import com.example.demo2.domain.validators.UtilizatorValidator;
import com.example.demo2.repository.PrietenieDbRepository;
import com.example.demo2.repository.PrietenieFileRepository;
import com.example.demo2.repository.UtilizatorFileRepository;
import com.example.demo2.repository.UtilizatorDbRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;




public class PrietenieService implements Service<Tuple<Long,Long>, Prietenie>{
    PrietenieDbRepository repo;
    UtilizatorDbRepository repoUtilizatori;

    String yellowColorCode = "\u001B[33m";

    String resetColorCode = "\u001B[0m";
    public PrietenieService(PrietenieDbRepository repo , UtilizatorDbRepository repoUtilizatori) {
        this.repo = repo;
        this.repoUtilizatori = repoUtilizatori;
        loadFriends();
    }


    public boolean createFriendRequest(Long id1 , Long id2 ) {
        try {
            Tuple<Long,Long> Id = new Tuple<Long,Long>(id1 , id2 );
            Optional<Prietenie> friendship = repo.findOne(Id);
            if (friendship.isPresent() && friendship.get().getCerere() != CererePrietenie.REJECTED)
                throw new Exception("The friend request exists and it is not rejected");
            else if (friendship.isPresent() && friendship.get().getCerere() == CererePrietenie.REJECTED) {
                friendship.get().setCerere(CererePrietenie.PENDING);
                repo.update(friendship.get());
                return true;
            } else {
                Optional<Utilizator> u1 = repoUtilizatori.findOne(id1);
                Optional<Utilizator> u2 = repoUtilizatori.findOne(id2);

                Prietenie newFriendship = new Prietenie();
                newFriendship.setId(new Tuple(id1, id2));
                newFriendship.setDate(LocalDateTime.now());
                newFriendship.setCerere(CererePrietenie.PENDING);

                repo.save(newFriendship);
                return true;
            }
        } catch (Exception e) {
            System.out.println(yellowColorCode + e.getMessage() + resetColorCode);
            return false;
        }
    }

    public boolean respondFriendRequest(Prietenie friendshipReq, CererePrietenie response) {
        try {
            if (!repo.findOne(friendshipReq.getId()).isPresent())
                throw new Exception("The friend request does not exist");
            else if (friendshipReq.getCerere() != CererePrietenie.PENDING)
                throw new Exception("The friend request is not pending");

            friendshipReq.setCerere(response);
            repo.update(friendshipReq);
            return true;
        } catch (Exception e) {
            System.out.println(yellowColorCode + e.getMessage() + resetColorCode);
            return false;
        }
    }






    private void loadFriends(){

        getAll().forEach(frienship->{
            Optional<Utilizator> user1 = repoUtilizatori.findOne(frienship.getId().getLeft());
            Optional<Utilizator> user2 = repoUtilizatori.findOne(frienship.getId().getRight());

            user1.get().addFriend(user2.get());
            user2.get().addFriend(user1.get());
        });


        /*
        for (Prietenie frienship: getAll()){
            Optional<Utilizator> user1 = repoUtilizatori.findOne(frienship.getId().getLeft());
            Optional<Utilizator> user2 = repoUtilizatori.findOne(frienship.getId().getRight());

            user1.get().addFriend(user2.get());
            user2.get().addFriend(user1.get());
        }

         */
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return repo.findAll();
    }


    private boolean checkMonth(String month, LocalDateTime data){
        //2023-11-23T13:53:15.866473700
        List<String> atr = List.of(data.toString().split("-"));
        String m = atr.get(1);
        if (m.equals(month))
            return true ;
        else
            return false;
    }

    public Iterable<Prietenie> getByMonth(Long userId,String month){
        Iterable<Prietenie> list = repo.findAll();
        List<Prietenie> ls = new ArrayList<Prietenie>();
        list.forEach(friend -> {
            if (friend.getId().getLeft() == userId || friend.getId().getRight() == userId )
            {
                if (checkMonth(month,friend.getDate())){
                    ls.add(friend);
                }
            }
        });
        return ls;
    }

    @Override
    public Prietenie getEntityById(Tuple<Long, Long> longLongTuple) {
        return repo.findOne(longLongTuple).get();
    }

    @Override
    public Prietenie add(Prietenie entity) {
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();
        Optional<Utilizator> user1 = repoUtilizatori.findOne(id1);
        if(user1 == null){
            throw new IllegalArgumentException("User inexistent!" + id1);
        }
        Optional<Utilizator> user2 = repoUtilizatori.findOne(id2);
        if(user2 == null){
            throw new IllegalArgumentException("User inexistent! " + id2);
        }
        user1.get().addFriend(user2.get());
        user2.get().addFriend(user1.get());
        //repoUtilizatori.update(user1.get());
       // repoUtilizatori.update(user2.get());
        return repo.save(entity).get();
    }
/*
    @Override public Prietenie delete(Tuple<Long, Long> longLongTuple) {
        return null;
    }


 */


    public Prietenie delete(Tuple<Long, Long> longLongTuple) {
        Long id1 = longLongTuple.getLeft();
        Long id2 = longLongTuple.getRight();
        Optional<Utilizator> u1 = repoUtilizatori.findOne(id1);
        Optional<Utilizator> u2 = repoUtilizatori.findOne(id2);
        if(u1 == null || u2 == null){
            throw new IllegalArgumentException("User inexistent!");
        }
        Prietenie deleted = repo.delete(longLongTuple).get();
        if(deleted != null) {
            u1.get().removeFriend(u2.get());
            u2.get().removeFriend(u1.get());
        }
        return deleted;
    }



}

