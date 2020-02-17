package com.example.hp.myapplication.Model;

public class User {

    private String Password;
    private String Mail;

    public User(){

    }

    public User(String mail, String password) {
        Mail = mail;
        Password = password;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password){
        Password = password;
    }
}
