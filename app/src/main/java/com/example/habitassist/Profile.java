package com.example.habitassist;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;


import com.google.android.gms.common.util.ArrayUtils;
import com.google.common.primitives.Ints;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This Class implements the profile object
 * it has parameters such as username, password, following and follow requests
 * all this information is stored and can be then placed into the database.
 */

public class Profile implements Serializable {
    private ArrayList<String> following;
    private String username;
    private String password;
    private ArrayList<String> followRequests;


    //different versions of creating a profile object
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

    //setters and getters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    //Gets the information and returns it as a hashmap allowing th einfo to be put into the database
    public HashMap<String, String> getDocument() {
        HashMap<String, String> profileDocument = new HashMap<>();
        profileDocument.put("username", username);
        profileDocument.put("password", password);
        profileDocument.put("followRequests", TextUtils.join("," ,followRequests));
        profileDocument.put("following", TextUtils.join("," ,following));
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

    //adds a follow request to follow request list
    public void AddFollowRequests(String follower) {
        this.followRequests.add(follower);
    }

    public void deleteFollower(String follower){this.following.remove(follower);}

    public void deleteFollowRequest(String follower){this.followRequests.remove(follower);}

    public void clearFollowRequests(){this.followRequests.clear();}

    public void clearFollowers(){this.following.clear();}
}
