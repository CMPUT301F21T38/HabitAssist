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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private Boolean isMyProfile;
    private String username;
    private boolean AtoZ = false;
    private boolean EarlyToLate = false;
    /**
     * This method sets the view, initializes variables, and assigns the Event Listeners.
     * It runs once immediately after entering this Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        MainActivity mainActivityInstance = MainActivity.getInstance();

        username = SingletonUsername.get();
        String usernameOfFollowing = getIntent().getStringExtra("usernameOfFollowing");
        if (usernameOfFollowing != null) {
            isMyProfile = false;
            username = usernameOfFollowing;
        }else{
            isMyProfile = true;
        }

        if (isMyProfile){
            findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.bottom_bar).setVisibility(View.GONE);
            findViewById(R.id.add_habit_button).setVisibility(View.GONE);
        }
        TextView usernameTitle = (TextView) findViewById(R.id.username);
        usernameTitle.setText("Habits of " + username);

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
                    String usernameFromUniqueId = doc.getId().split("\\*")[0];
                    if (usernameFromUniqueId.equals(username)) {
                        Habit habit = Habit.parseFromDoc(doc.getData());
                        if (habit != null && (isMyProfile || habit.isPublic())){
                            profileAllHabitsList.add(habit);
                            profileAllHabitsTitleList.add(habit.getHabitTitle());
                        }
                    }
                }
                profile_habitAdapter.notifyDataSetChanged();

                // Dynamically update height
                ViewGroup.LayoutParams params = profileListView.getLayoutParams();
                float dpFactor = getApplicationContext().getResources().getDisplayMetrics().density;
                int dpPerItem = 51;
                int offsetInDp = 80;
                int nHabits = profileAllHabitsList.size();
                int height =  (int) ((nHabits * dpPerItem + offsetInDp) * dpFactor);
                params.height =  nHabits == 0 ? 0 : height;
                profileListView.setLayoutParams(params);
                profileListView.requestLayout();
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
                String titleOfClickedHabit = profileAllHabitsTitleList.get(position);

                // Habit habitClickedOn = null;
                // for (Habit habit : profileAllHabitsList) {
                //     if (habit.getHabitTitle().equals(titleOfClickedHabit)) {
                //         habitClickedOn = habit;
                //         break;
                //     }
                // }

                Habit habitClickedOn = profileAllHabitsList.get(position);

                Intent habitDetailIntent = new Intent(ProfileActivity.this, HabitDetailActivity.class);
                habitDetailIntent.putExtra("habitPassed", habitClickedOn);
                habitDetailIntent.putExtra("isMyHabit", isMyProfile.toString());
                habitDetailIntent.putExtra("habitOwnerUsername", username);
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
        Intent intent = new Intent(this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }

    /**
     * This method is called when the Home button on the activity_profile.xml page is clicked
     * @param view
     */
    public void HomeButton(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }


    public void Logout(View view){
        new AlertDialog.Builder(this)
                .setTitle("Logging out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SingletonUsername.initialize(null);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (isMyProfile) {
            new AlertDialog.Builder(this)
                    .setTitle("Exiting HabitAssist")
                    .setMessage("Are you sure you want to exit the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            finish();
        }
    }

    public void TimeOrder(View view) {
        int minus, plus;
        if (EarlyToLate == false) {
            minus = 1;
            plus = -1;
            ((Button) findViewById(R.id.StartDate)).setText("Late-Early");
            ((TextView) findViewById(R.id.sortedby)).setText("Reordered manually from: Earliest startDate to latest");
        } else {
            minus = -1;
            plus = 1;
            ((Button) findViewById(R.id.StartDate)).setText("Early-Late");
            ((TextView) findViewById(R.id.sortedby)).setText("Reordered manually from: Latest startDate to earliest");
        }

        EarlyToLate = !EarlyToLate;

        // future to past order
        Collections.sort(profileAllHabitsList, (h1, h2) -> {
            Date h1StartDate = null, h2StartDate = null;
            try {
                h1StartDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(h1.getStartDate());
                h2StartDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(h2.getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int returnVal = 0;
            if (h1StartDate.after(h2StartDate)) {
                returnVal = minus;
            } else if (h2StartDate.after(h1StartDate)) {
                returnVal = plus;
            }
            return returnVal;
        });

        for (int i = 0; i < profileAllHabitsList.size(); i++) {
            profileAllHabitsTitleList.set(i, profileAllHabitsList.get(i).getHabitTitle());
        }
        profile_habitAdapter.notifyDataSetChanged();
        AtoZ = false;
        ((Button) findViewById(R.id.AtoZ)).setText("A-Z");

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AlphabeticalOrder(View view){
        //Use allHabitsTitleList
        if (AtoZ == false) {
            profileAllHabitsTitleList.sort(Comparator.comparing(String::toString));
            profile_habitAdapter.notifyDataSetChanged();
            AtoZ = true;
            Button rename = (Button) view.findViewById(R.id.AtoZ);
            rename.setText("Z-A");
            Collections.sort(profileAllHabitsList, Comparator.comparing(Habit::getHabitTitle));
            ((TextView) findViewById(R.id.sortedby)).setText("Reordered manually from: A to Z");
            EarlyToLate = false;

        }else{
            profileAllHabitsTitleList.sort(Comparator.comparing(String::toString).reversed() );
            profile_habitAdapter.notifyDataSetChanged();
            AtoZ = false;
            Button rename = (Button) view.findViewById(R.id.AtoZ);
            rename.setText("A-Z");
            Collections.sort(profileAllHabitsList, (h1, h2) -> h2.getHabitTitle().compareTo(h1.getHabitTitle()));
            ((TextView) findViewById(R.id.sortedby)).setText("Reordered manually from: Z to A");
            EarlyToLate = false;
        }

    }
}