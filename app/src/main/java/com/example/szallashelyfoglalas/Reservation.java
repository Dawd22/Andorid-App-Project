package com.example.szallashelyfoglalas;

import java.util.Date;

public class Reservation {
    Date firstday;
    Date lastday;
    String id;
    int full_price;
    String room_hotel;
    String room_id;
    String room_type;
    String user_email;

    public Reservation() {
    }

    public Reservation(Date firstday, Date lastday, String id, int full_price, String room_hotel, String room_id, String room_type, String user_email) {
        this.firstday = firstday;
        this.lastday = lastday;
        this.id = id;
        this.full_price = full_price;
        this.room_hotel = room_hotel;
        this.room_id = room_id;
        this.room_type = room_type;
        this.user_email = user_email;
    }

    public Date getFirstday() {
        return firstday;
    }

    public void setFirstday(Date firstday) {
        this.firstday = firstday;
    }

    public Date getLastday() {
        return lastday;
    }

    public void setLastday(Date lastday) {
        this.lastday = lastday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFull_price() {
        return full_price;
    }

    public void setFull_price(int full_price) {
        this.full_price = full_price;
    }

    public String getRoom_hotel() {
        return room_hotel;
    }

    public void setRoom_hotel(String room_hotel) {
        this.room_hotel = room_hotel;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
