package com.example.habitassist;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile implements Serializable {
    private ArrayList<String> following;
    private String username;
     private String password;
     private ArrayList<String> followRequests;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    Profile(String username, String password){
        this.username = username;
        this.password = password;
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
    }
    Profile(){
        this.username = "";
        this.password = "";
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HashMap<String, String> getDocument() {
        HashMap<String, String> profileDocument = new HashMap<>();
        profileDocument.put("username", username);
        profileDocument.put("password", password);
        profileDocument.put("followRequests", String.join("," ,followRequests));
        profileDocument.put("following", String.join("," ,following));
        return profileDocument;
    }


    public ArrayList<String> getFollowing() {
        return this.following;
    }

    public void addFollowing(String follow) {
        this.following.add(follow);
    }

    public ArrayList<String> getFollowRequests() {
        return this.followRequests;
    }

    public void AddFollowRequests(String follower) {
        this.followRequests.add(follower);
    }

    public void clearFollowRequests(){this.followRequests.clear();}

    public void clearFollowers(){this.following.clear();}
}
