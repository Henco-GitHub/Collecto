package com.example.collecto;

public class login {

    private String username;
    private String password;
    private int id;

    //constructor
    public login(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //constructor with parameters
    public login(String username, String password, int id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

}
