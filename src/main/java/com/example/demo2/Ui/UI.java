package com.example.demo2.Ui;



import com.example.demo2.domain.CererePrietenie;
import com.example.demo2.domain.Prietenie;
import com.example.demo2.domain.Tuple;
import com.example.demo2.domain.Utilizator;
import com.example.demo2.domain.validators.PrietenieValidator;
import com.example.demo2.domain.validators.UtilizatorValidator;
import com.example.demo2.repository.*;
import com.example.demo2.service.PrietenieService;
import com.example.demo2.service.UtilizatorService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class UI {

   // UtilizatorFileRepository userFileRepo = new UtilizatorFileRepository("src/Users.txt", new UtilizatorValidator());
        UtilizatorDbRepository userFileRepo = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","Fane1234");
   // PrietenieFileRepository prietenieFileRepository = new PrietenieFileRepository("src/Prietenii.txt", new PrietenieValidator());
    PrietenieDbRepository prietenieFileRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","Fane1234");

    MessageDBRepository m = new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","Fane1234",userFileRepo);
    UtilizatorService utilizatorService = new UtilizatorService(userFileRepo , prietenieFileRepository, m);
    PrietenieService prietenieService = new PrietenieService(prietenieFileRepository, userFileRepo);

    public static UI instance;
    public static UI getInstance(){
        if(instance == null) instance = new UI();
        return instance;
    }
    private UI(){}

    public void run(){
        while(true){
            meniu();
            Scanner in = new Scanner(System.in);

            int option = in.nextInt();
            if(option == 1) addUser();
            else if(option == 2) deleteUser();
            else if(option == 3) printAllUsers();
            else if(option == 4) createFriendship();
            else if(option == 5) deleteFriendship();
            else if(option == 6) printAllPrietenii();
            else if(option == 7) System.out.println("Exista: " + utilizatorService.nrComunitati() + " comunitati !");
            else if(option == 8) System.out.println("Cea mai sociabila retea: " + utilizatorService.comunitateaSociabila());
            else if (option == 9) printPrieteniiLuna();
            else if (option == 0){
                break;
            }
        }
    }

    private void meniu(){
        System.out.println("MENIU: ");
        System.out.println("1.Adauga Utilizator");
        System.out.println("2.Sterge Utilizator");
        System.out.println("3.Afiseaza Utilizatori");
        System.out.println("4.Creeaza prietenie");
        System.out.println("5.Sterge prietenie");
        System.out.println("6.Afiseaza prietenii");
        System.out.println("7.Numar comunitati");
        System.out.println("8.Cea mai sociabila comunitate");
        System.out.println("9.Prieteniile unui utilizator dintr-o luna data");
        System.out.println("0.Exit");
    }

    private void addUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("Prenume: ");
        String fName = in.next();
        System.out.println("Nume: ");
        String lName = in.next();
        System.out.println("ID: ");
        Long id = in.nextLong();
        Utilizator user = new Utilizator(fName, lName);
        user.setId(id);
        try {
            Utilizator addedUser = utilizatorService.add(user);
            if (addedUser == null) {
                System.out.println("Utilizatorul " + fName + " " + lName + " a fost salvat!");
            } else {
                System.out.println("Exista deja un utilizator cu ID-ul " + id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteUser() {
        Scanner in = new Scanner(System.in);
        System.out.println("ID: ");
        Long id = in.nextLong();
        try {

            Utilizator deletedUser = utilizatorService.delete(id);
            if (deletedUser != null) {
                System.out.println("Utilizatorul " + deletedUser.getFirstName() + " " + deletedUser.getLastName() + " a fost sters!");
                //prietenieService.deleteNonexistent(deletedUser.getId());
            } else {
                System.out.println("Nu exista utilizatorul cu ID-ul " + id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printAllUsers(){
        Iterable<Utilizator> allUsers = utilizatorService.getAll();
        for(Utilizator user : allUsers) {
            System.out.println(user);
        }
    }

    private void printAllPrietenii() {
        Iterable<Prietenie> allPrieteni = prietenieService.getAll();

        for (Prietenie prietenie : allPrieteni) {
            LocalDateTime d = prietenie.getDate();
            CererePrietenie status = prietenie.getCerere();
            System.out.println(utilizatorService.getEntityById(prietenie.getId().getLeft())  +" este prieten cu " + utilizatorService.getEntityById(prietenie.getId().getRight()) +" Din data de:"+ d + "Status: " + status);

        }
    }

    private void printPrieteniiLuna(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID: ");
        Long usr = in.nextLong();
        System.out.println("Luna: ");
        String month = in.next();


        Iterable<Prietenie> allPrieteni = prietenieService.getByMonth(usr,month);
        for (Prietenie prietenie : allPrieteni) {
            LocalDateTime d = prietenie.getDate();
            if (utilizatorService.getEntityById(prietenie.getId().getLeft() ).getId() == usr)
                System.out.println(utilizatorService.getEntityById(prietenie.getId().getRight()) +" Din data de:"+ d );
            else
                System.out.println(utilizatorService.getEntityById(prietenie.getId().getLeft()) +" Din data de:"+ d );
        }
    }

    private void createFriendship(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID user 1: ");
        Long id1 = in.nextLong();
        System.out.println("ID user 2: ");
        Long id2 = in.nextLong();
        Prietenie newFriendship = new Prietenie();
        newFriendship.setId(new Tuple(id1, id2));
        newFriendship.setDate(LocalDateTime.now());
        try {
            Prietenie prietenieAdd = prietenieService.add(newFriendship);
            if(prietenieAdd == null) System.out.println("Prietenie creata cu succes!");
        }
        catch (Exception e) { System.out.println(e.getMessage()); }
    }

    private void deleteFriendship(){
        Scanner in = new Scanner(System.in);
        System.out.println("ID utilizator 1: ");
        Long id1 = in.nextLong();
        System.out.println("ID utilizator 2:");
        Long id2 = in.nextLong();
       /*
        Optional<Prietenie> p =  prietenieFileRepository.findOne(new Tuple<>(id1,id2));
          try {
              System.out.println(p.get().getId().getRight() + " " + p.get().getDate() + " " + p.get().getId().getLeft());

          }
          catch (Exception e){
              System.out.println(e.getMessage());
          }



        */

        try{
            Prietenie deletedPrietenie1 = prietenieService.delete(new Tuple<>(id1, id2));
            Prietenie deletedPrietenie2 = prietenieService.delete(new Tuple<>(id2, id1));

            if(deletedPrietenie1 != null || deletedPrietenie2 != null)
                System.out.println("Prietenia a fost stearsa cu succes!");
            else System.out.println("Eroare! Utilizatorii nu sunt prieteni");

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }


    }

}
