/*
 * This file implements the Android Activity called ProfileActivity
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * The Android Activity that handles showing the user profile and with a list of all habits for
 * a profile. It enables navigating to the page for adding new habits and also to the detail page of
 * any clicked habit.
 */
public class ProfileActivity extends AppCompatActivity {
    /** A ListView used to show the titles of all habits on hte profile page */
    private ListView profileListView;
    /** A list of all the habits belonging to the profile */
    private ArrayList<Habit> profileAllHabitsList;
    /** A List of all the titles of the habits belonging to the profile */
    private ArrayList<String> profileAllHabitsTitleList;
    /** An array adapter used to show the titles of all habits into a ListView */
    private ArrayAdapter<String> profile_habitAdapter;

    /** A reference to the Firestore database */
    private FirebaseFirestore db;

    /**
     * This method sets the view, initializes variables, and assigns the Event Listeners.
     * It runs once immediately after entering this Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize variables
        profileListView = (ListView) findViewById(R.id.profile_listview);
        profileAllHabitsList = new ArrayList<>();
        profileAllHabitsTitleList = new ArrayList<>();

        // Add listener that reacts to changes to the Firestore database and updates the local UI
        db = FirebaseFirestore.getInstance();
        db.collection("habits").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                profileAllHabitsList.clear();
                profileAllHabitsTitleList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    String title = (String) data.get("title");
                    String reason = (String) data.get("reason");
                    String startDate = (String) data.get("startDate");
                    String daysToBeDone = (String) data.get("daysToBeDone");
                    // Add a guard in case a wrongly structured Habit data is put into firestore
                    if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                        Habit habit = new Habit(title, reason, startDate, daysToBeDone, MainActivity.getUsername());
                        profileAllHabitsList.add(habit);
                        profileAllHabitsTitleList.add(title);
                    }
                }
                profile_habitAdapter.notifyDataSetChanged();
            }
        });

        // Connect the habit titles data list to the user-interface with an ArrayAdapter
        profile_habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, profileAllHabitsTitleList);
        profileListView.setAdapter(profile_habitAdapter);

        // Add listener that navigates to the correct Habit detail page when a habit is clicked
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get habit clicked from the ListView
                Habit habitClickedOn = profileAllHabitsList.get(position);

                Intent habitDetailIntent = new Intent(ProfileActivity.this, HabitDetailActivity.class);
                habitDetailIntent.putExtra("habitPassed", habitClickedOn);
                startActivity(habitDetailIntent);
            }
        });
    }

    /**
     * This method is called when the Plus button on the activity_profile.xml page is clicked
     * @param view
     */
    public void addHabitButtonClickHandler(View view) {
        startActivity(new Intent(ProfileActivity.this, AddHabitActivity.class));
    }

    /**
     * This method is called when the Feed button on the activity_profile.xml page is clicked
     * @param view
     */
    public void FeedButton(View view) {

    }

    /**
     * This method is called when the Home button on the activity_profile.xml page is clicked
     * @param view
     */
    public void HomeButton(View view) {
        finish();
    }

    public void Logout(View view){

        MainActivity mainActivityInstance = MainActivity.getInstance();

        mainActivityInstance.Logout(view);
        finish();
    }
}