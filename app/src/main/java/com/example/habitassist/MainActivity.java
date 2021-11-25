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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.io.Serializable;
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

    /**
     * This variable is set immediately after a ListView of habits is clicked. It stores the title
     * of the habit clicked. The unique title is used to identify which habit object to delete or
     * edit if an action like that is triggered. */
    public String DeleteAndEdit;


    int profileCode = 42;

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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, profileCode);


        // Initialize variables
        instance = this;
        ListView listview = (ListView) findViewById(R.id.listview);
        habitList = new ArrayList<>();
        habitTitleList = new ArrayList<>();

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // Add listener that reacts to changes to the Firestore database and updates the local UI
        CollectionReference habitsCollection = db.collection("habits");
        habitsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                habitList.clear();
                habitTitleList.clear();
                for (QueryDocumentSnapshot doc: value) {
                    String uniqueId = doc.getId().split("\\*")[0];

                    if (uniqueId.equals(getUsername())) {
                        Map<String, Object> data = doc.getData();
                        String title = (String) data.get("title");
                        String reason = (String) data.get("reason");
                        String startDate = (String) data.get("startDate");
                        String daysToBeDone = (String) data.get("daysToBeDone");
                        if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                            Habit habit = new Habit(title, reason, startDate, daysToBeDone, username);
                            habitList.add(habit);
                            if (habit.isForToday()) {
                                habitTitleList.add(title);
                            }
                        }
                    }
                }
                habitAdapter.notifyDataSetChanged();
            }
        });




        // Connect the habit titles data list to the user-interface with an ArrayAdapter
        habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, habitTitleList);
        listview.setAdapter(habitAdapter);

        // Navigate to the AddHabitActivity page when the Plus button is clicked
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.add_habit_button);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddMainHabit();
            }
        });

        // Add listener that navigates to the correct Habit detail page when a habit is clicked
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // it returns the name of the habit from the Listview OR the ArrayList
                DeleteAndEdit = habitTitleList.get(i);
                Intent intent = new Intent(MainActivity.this, HabitDetailActivity.class);
                String uniqueTitle = habitTitleList.get(i);
                Habit habitPassed = habitList.get(i); // Not necessarily the correct Habit object

                for (Habit habit : habitList) {
                    if (habit.getHabitTitle().equals(uniqueTitle)) {
                        habitPassed = habit;
                        break;
                    }
                }
                intent.putExtra("habitPassed", habitPassed);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == profileCode) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                setUsername(result);
                //get the username and all the habits associated with that username
                //take the first habit and update its title to be the same as it once was
                // add a temp document to update the habits collection
                Map<String, Object> temp = new HashMap<>();
                temp.put("temp", "temp");
                db.collection("habits").document("temp")
                        .set(temp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                // delete temp
                db.collection("habits").document("temp")
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //return to login screen
            }
        }
    }

    /**
     * This method returns an instance of the running MainActivity `this` context;
     * @return
     */
    public static MainActivity getInstance(){
        return instance;
    }

    /**
     * This method deletes the habit with title stored in the DeleteAndEdit variable.
     * @param view
     */
    public void DeleteHabit(View view){
        String toDeleteOrEdit = username + "*" + DeleteAndEdit;
        db.collection("habits").document(toDeleteOrEdit)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * This method is called when the Edit button on the HabitDetailActivity page is called.
     * It navigates to the HabitEditActivity page.
     * @param view
     */
    public void EditHabit(View view) {
        Intent intent= new Intent(this, HabitEditActivity.class);

        // Look for the habit with the same title as DeleteAndEdit
        Habit habitPassedIn = null;
        for (Habit habit : habitList) {
            if (habit.getHabitTitle().equals(DeleteAndEdit)) {
                habitPassedIn = habit;
                break;
            }
        }
        if (habitPassedIn != null) {
            intent.putExtra("habitPassedIn", (Serializable) habitPassedIn);
            startActivity(intent);
        }
    }

    /**
     * This method is called when the Plus button on the activity_main.xml page is clicked
     * It navigates to the AddHabitActivity page
     */
    public void AddMainHabit(){
        Intent intent = new Intent(this, AddHabitActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the Profile button on the activity_main.xml page is clicked.
     * It navigates to the profile page.
     * @param view
     */
    public void ProfileButton(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("habit2", habitList);
        startActivity(intent);
    }

    /**
     * This method is called when the Feed button on the activity_main.xml page is clicked
     * @param view
     */
    public void FeedButton(View view){

    }

    /**
     * This method is called when the Home button on the activity_main.xml page is clicked
     * @param view
     */
    public void HomeButton(View view){
        // do something when the home button is clicked
    }

    public void Logout(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, profileCode);
    }

    public void setUsername(String new_username){
        username = new_username;
    }

    public String getUsername(){
        return username;
    }
}
