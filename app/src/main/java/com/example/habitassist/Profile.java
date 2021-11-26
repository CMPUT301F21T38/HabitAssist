package com.example.habitassist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile implements Serializable {
    private ArrayList<String> following;
    private String username;
     private String password;
     private ArrayList<String> followRequests;

    Profile(String username, String password){
        this.username = username;
        this.password = password;
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
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


    public ArrayList<String> getFollowing() {
        return following;
    }

    public void addFollowing(String follow) {
        this.following.add(follow);
    }

    public ArrayList<String> getFollowRequests() {
        return followRequests;
    }

    public void AddFollowRequests(String follower) {
        this.followRequests.add(follower);
    }
}
