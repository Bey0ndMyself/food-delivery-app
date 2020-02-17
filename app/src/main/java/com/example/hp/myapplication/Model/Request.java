package com.example.hp.myapplication.Model;

import java.util.List;

public class Request {

    private String mail;
    private String address;
    private String total;
    private List<Order> foods;

    public Request() {

    }

    public Request(String mail, String address, String total, List<Order> foods) {
        this.mail = mail;
        this.address = address;
        this.total = total;
        this.foods = foods;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
