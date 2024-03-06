package com.example.demo2.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private ArrayList<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
    }
    public void addFriend(Utilizator e) throws IllegalArgumentException{
        if(friends.contains(e))
            throw new IllegalArgumentException("Prietenie Existenta!");
        friends.add(e);
    }

    public void removeFriend(Utilizator e) throws IllegalArgumentException{
        if(!friends.contains(e))
            throw new IllegalArgumentException("Utilizatori Dusmani!");
        friends.remove(e);
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "Utilizator{"+ "ID"+
                getId() +
                "Nume='" + firstName + '\'' +
                ", Prenume='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }

}
