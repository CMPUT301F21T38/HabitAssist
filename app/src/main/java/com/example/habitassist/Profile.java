package com.example.habitassist;

import java.io.Serializable;

public class Profile implements Serializable {
     private String username;
     private String password;


    Profile(String username, String password){
        this.username = username;
        this.password = password;

    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

}
