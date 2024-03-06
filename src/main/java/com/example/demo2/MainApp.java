package com.example.demo2;/*
import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import domain.validators.PrietenieValidator;
import domain.validators.UtilizatorValidator;
import repository.InMemoryRepository;

public class Main {

    public static void main(String[] args) {
    //domain

        Utilizator u1=new Utilizator("Pop","Ana");u1.setId(1l);
        Utilizator u2=new Utilizator("Alen", "Vlad"); u2.setId(1l);
        System.out.println(u1);
        System.out.println(u2);
        assert(u1.getFirstName() == "Pop");
        u1.addFriend(u2);
        System.out.println( u1.getFriends());

       // u2.setId(2l);
        Prietenie prietenie = new Prietenie();
        prietenie.setId(new Tuple<>(u1.getId(),u2.getId()));
        assert(prietenie.getId().getRight() == 1l);
        assert(prietenie.getId().getLeft() == 1l);

        //repo pt utilizator+prieteni
        //daca am acelasi id nu il va adauga
        Utilizator u1=new Utilizator("Pop","Ana");u1.setId(1l);
        Utilizator u2=new Utilizator("Alen", "Vlad"); u2.setId(11l);

        InMemoryRepository<Long, Utilizator> repo=new InMemoryRepository<>(new UtilizatorValidator());
        repo.save(u1);
        repo.save(u2);
        //repo.save(u2);
        System.out.println(repo.findAll());

        InMemoryRepository<Tuple<Long,Long>, Prietenie> repo2=new InMemoryRepository<>(new PrietenieValidator());
        Prietenie p = new Prietenie();
        p.setId(new Tuple<>(u1.getId(),u2.getId()));
        repo2.save(p);
        System.out.println(repo2.findAll());

    }
}*/
import com.example.demo2.Ui.UI;

//diagrama e in overview summary
public class
MainApp {

    public static void main(String[] args) {
        //runTests();

        UI ui = UI.getInstance();
        ui.run();
    }

}
