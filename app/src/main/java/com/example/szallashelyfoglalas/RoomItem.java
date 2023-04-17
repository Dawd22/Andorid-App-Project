package com.example.szallashelyfoglalas;

public class RoomItem {
    private String hotel;
    private String id;
    private Location location;
    private int price;
    private String type;

    public RoomItem() {
    }

    public RoomItem(String hotel, String id, Location location, int price, String type) {
        this.hotel = hotel;
        this.id = id;
        this.location = location;
        this.price = price;
        this.type = type;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
