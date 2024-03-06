package com.example.demo2.domain;




import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private CererePrietenie cerere;
    public Prietenie() {};

    public LocalDateTime getDate() {
        return date;
    }


    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public CererePrietenie getCerere(){return cerere;}

    public void  setCerere (CererePrietenie c ) {
        cerere = c ;
    }

    @Override
    public String toString() {
        return  "ID=" + id +
                " DATA=" + date.getMonth() +
                " STATUS=" + cerere

               ;
    }
}