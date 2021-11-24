package com.example.habitassist;

import java.io.Serializable;
import java.util.HashMap;

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

    public HashMap<String, String> getDocument() {
        HashMap<String, String> profileDocument = new HashMap<>();
        profileDocument.put("username", username);
        profileDocument.put("password", password);
        return profileDocument;
    }
}
