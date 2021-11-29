/*
 * This file implements the Android Activity called MainActivity
 * -------------------------------------------------------------------------------------------------
 *
 * Copyright [2021] [CMPUT301F21T38]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.habitassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main Android Activity and the entry point of the app. It handles showing the home page with
 * habits to do today. It also enables navigating to the page for adding new habits and also to the
 * detail page of any clicked habit.
 */
public class MainActivity extends AppCompatActivity {

    /** A list of the habits belonging to the profile that needs to be done today */
    private ArrayList<Habit> habitList;
    /** A list of the titles of the habits belonging to the profile that needs to be done today */
    private ArrayList<String> habitTitleList;
    /** An array adapter used to show the titles in habitTitleList into a ListView */
    private ArrayAdapter<String> habitAdapter;

    /** A reference to the Firestore database  */
    public FirebaseFirestore db;

    /** A name tag used in logging statements from this activity */
    final String TAG = "MainActivity";

    private static WeakReference<Context> singletonInstanceReference;


    int profileCode = 42;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private String username;
    /** Instance of the current running MainActivity `this` context */
    private static MainActivity instance;


    /**
     * This method sets the view, initializes variables, and assigns the Event Listeners.
     * It runs once immediately after entering this Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        instance = this;
        singletonInstanceReference = new WeakReference<>(getApplicationContext());

        usernameEditText =  (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.Password);
        SingletonUsername.initialize(null);
    }

    //This tells where the app opens to after login
    @Override
    protected void onStart() {
        super.onStart();
        if (SingletonUsername.get() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        }
    }

    /**
     * This method is called when the user hits the login button.
     * if the username and password is correct the user is logged in
     * otherwise, they are told which field is incorrect
     * @param view
     */
    public void LoginButton(View view){
        String usernameEntered = usernameEditText.getText().toString();
        String passwordEntered = passwordEditText.getText().toString();

        if (!usernameEntered.equals("")) {
            db.collection("profiles")
                    .document(usernameEntered)
                    .get()
                    .addOnSuccessListener((document) -> {
                        if (document != null && document.exists()) {
                            if (document.get("password").equals(passwordEntered)) {
                                setUsername(usernameEntered);
                                SingletonUsername.initialize(usernameEntered);
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username does not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is called when the user hits the CreateAccount button.
     * the username and password are identified and the user is asked to validate their selected password
     * if they say yes they are continued into the app as if they were logging in otherwise the
     * user is returned to the login screen.
     * @param view
     */
    public void CreateAccount(View view){
        String usernameEntered = usernameEditText.getText().toString();
        String passwordEntered = passwordEditText.getText().toString();
        new AlertDialog.Builder(this)
                .setTitle("Creating Account")
                .setMessage("Are you sure you want your password to be " + passwordEntered)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // here
                        DocumentReference docRef = db.collection("profiles").document(usernameEntered);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(getApplicationContext(), "Username already taken, please try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Profile newProfile = new Profile(usernameEntered, passwordEntered);
                                        String name = newProfile.getUsername();
                                        HashMap<String, String> profileDocument = newProfile.getDocument();
                                        db.collection("profiles").document(name).set(profileDocument);

                                        setUsername(usernameEntered);

                                        SingletonUsername.initialize(usernameEntered);

                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    }
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)

                .show();


    }

    /**
     * This method returns an instance of the running MainActivity `this` context;
     * @return
     */
    public static MainActivity getInstance(){
        return instance;
    }

    //This sets the username for the user of the account
    public void setUsername(String new_username){
        username = new_username;
    }

    //This allows access to the username throughout the application
    public String getUsername(){
        return username;
    }
}
