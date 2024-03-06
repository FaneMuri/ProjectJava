package com.example.demo2.service;



import com.example.demo2.domain.*;
import com.example.demo2.domain.validators.UtilizatorValidator;
import com.example.demo2.repository.MessageDBRepository;
import jdk.jshell.execution.Util;
import com.example.demo2.repository.PrietenieDbRepository;
import com.example.demo2.repository.UtilizatorFileRepository;
import com.example.demo2.repository.UtilizatorDbRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilizatorService implements Service<Long, Utilizator>{

    private UtilizatorDbRepository repo;
    private PrietenieDbRepository rep;
    private MessageDBRepository messageRepo;

    public UtilizatorService(UtilizatorDbRepository repo , PrietenieDbRepository rep , MessageDBRepository m ){
        this.repo = repo;
        this.rep = rep;
        this.messageRepo = m ;
    }

    @Override
    public Utilizator add(Utilizator entity){
        return repo.save(entity).get();
    }

    @Override
    public Utilizator delete(Long id){
        return repo.delete(id).get();
    }

    @Override
    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    @Override
    public Utilizator getEntityById(Long id) {
        return repo.findOne(id).get();
    }

    private void DFS(Utilizator utilizator,Map<Utilizator, Integer> visited, int len)
    {
        visited.put(utilizator, len);
        getFriends(utilizator.getId()).forEach(user->{
            if(!visited.containsKey(user) || visited.get(user) == 0){
                DFS(user, visited, len);
            }
        });
        /*
        for (Utilizator user : utilizator.getFriends()) {
            if(!visited.containsKey(user) || visited.get(user) == 0){
                DFS(user, visited, len);
            }
        }

         */
    }

    public int nrComunitati(){

        Map<Utilizator, Integer> visited = new HashMap<>();
        final int[] numar_comunitati = {0};

        getAll().forEach(user->{
            if(!visited.containsKey(user) || visited.get(user) == 0){
                numar_comunitati[0]++;
                DFS(user, visited, numar_comunitati[0]);


            }
        });

        /*
        for(Utilizator user : getAll())
            if(!visited.containsKey(user) || visited.get(user) == 0){
                numar_comunitati[0]++;
                DFS(user, visited, numar_comunitati[0]);
            }
*/
        return numar_comunitati[0];
    }


    public Iterable<Utilizator> getFriends(Long userId){
        Iterable<Prietenie> list = rep.findAll();
        List<Utilizator> ls = new ArrayList<Utilizator>();
        list.forEach(friend -> {
            if (friend.getId().getLeft() == userId  )
            {

                    ls.add(getEntityById(friend.getId().getRight()));

            }
            else if (friend.getId().getRight() == userId)
            {
                ls.add(getEntityById(friend.getId().getLeft()));
            }
        });
        return ls;
    }
    private int BFS(Utilizator utilizator, Map<Utilizator, Integer> visited){

        int maxim = -1;
        Queue<Utilizator> coada = new LinkedList<>();
        coada.add(utilizator);
        visited.put(utilizator,1);

        while(!coada.isEmpty()){
            Utilizator nextUtilizator = coada.peek();
            coada.poll();
            for(Utilizator user : getFriends(nextUtilizator.getId())){
                if(!visited.containsKey(user) || visited.get(user) == 0) {
                    int nxt_value = visited.get(nextUtilizator) + 1;

                    if(nxt_value > maxim) maxim = nxt_value;

                    visited.put(user, nxt_value);
                    coada.add(user);
                }
            }
        }

        return maxim;
    }

    private void populeaza(Utilizator utilizator,List<Utilizator> list){
        getFriends(utilizator.getId()).forEach(user->{
            if(!list.contains(user)){
                list.add(user);
                populeaza(user,list);
            }
        });

        /*
        for(Utilizator user : utilizator.getFriends()){
            if(!list.contains(user)){
                list.add(user);
                populeaza(user,list);
            }
        }

         */
    }


    public List<Utilizator> comunitateaSociabila(){
        Map<Utilizator, Integer> visited = new HashMap<>();
        List<Utilizator> result = new ArrayList<>();
        final int[] maxim = {-1};

        getAll().forEach(user->{
            if(!visited.containsKey(user) || visited.get(user) == 0){
                int lung = BFS(user, visited);
                if(lung > maxim[0]){
                    maxim[0] = lung;
                    if(!result.isEmpty()) result.clear();
                    populeaza(user,result);
                }
            }



        });
        /*
        for(Utilizator user : getAll())
            if(!visited.containsKey(user) || visited.get(user) == 0){
                int lung = BFS(user, visited);
                if(lung > maxim){
                    maxim = lung;
                    if(!result.isEmpty()) result.clear();
                    populeaza(user,result);
                }
            }
*/
        return result;

    }

    /////////////////////// MESSAGE //////////////////////


    public boolean addMessage(Long id_from, List<Long> id_to, String message) {
        try {
            Utilizator from = repo.findOne(id_from).get();
            List<Utilizator> toUsers = new ArrayList<>();
            for (Long  id : id_to) {
                Utilizator u = repo.findOne(id).get();
                if(u != null) {
                    toUsers.add(u);
                } else {
                    throw new Exception("ID does not exist");
                }
            }

            if (from == null || toUsers.isEmpty())
                throw new Exception("Email does not exist");
            if (Objects.equals(message, ""))
                throw new Exception("Message is empty");

            Message msg = new Message(from, toUsers, message);
            messageRepo.save(msg);

//            List<Message> messagesTwoUsers = getMessagesBetweenTwoUsers(email_to, email_from);
//            if (messagesTwoUsers.size() > 1) {
//                Message secondToLastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 2);
//                Message lastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 1);
//                secondToLastMessage.setReplyTo(lastMessage);
//                messageRepo.update(secondToLastMessage);
//            }

            return true;
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return false;
        }
    }


    public boolean addMessage(Long id_from, List<Long> id_to, String message, Message replyTo) {
        try {
            Utilizator from = repo.findOne(id_from).get();
            List<Utilizator> toUsers = new ArrayList<>();
            for (Long id : id_to) {
                Utilizator u = repo.findOne(id).get();
                if(u != null) {
                    toUsers.add(u);
                } else {
                    throw new Exception("ID does not exist");
                }
            }

            if (from == null || toUsers.isEmpty())
                throw new Exception("Email does not exist");
            if (Objects.equals(message, ""))
                throw new Exception("Message is empty");

            Message msg = new Message(from, toUsers, message, replyTo);
            messageRepo.save(msg);

            return true;
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return false;
        }
    }


    public ArrayList<Message> getMessagesBetweenTwoUsers(Long id1, Long id2) {
        if (repo.findOne(id1).isPresent() && repo.findOne(id2).isPresent()) {

            Collection<Message> messages = (Collection<Message>) messageRepo.findAll();
            return messages
                    .stream()
                    .filter(x -> (x.getFrom().getId() ==  id1 && userFoundById(x.getTo(), id2)) ||
                            (x.getFrom().getId() ==id2 && userFoundById(x.getTo(), id1)))
                    .sorted(Comparator.comparing(Message::getDate))
                    .collect(Collectors.toCollection(ArrayList::new));


        }
        return new ArrayList<>();
    }

    public boolean updateMessage(Message msg) {
        messageRepo.update(msg);
        return true;
    }

    boolean userFoundById(List<Utilizator> users, Long id) {
        for (Utilizator u: users) {
            if (u.getId() == id)
                return true;
        }

        return false;
    }






}
