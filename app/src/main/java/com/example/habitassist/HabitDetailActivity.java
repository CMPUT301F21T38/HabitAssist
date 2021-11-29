/*
 * This file implements the Android Activity called HabitDetailActivity
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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Android Activity that handles displaying the details of a particular habit and deleting it.
 * This Activity also enables navigating to the activity where the habit details can be edited.
 *
 *  Currently outstanding issues: Always shows the Edit and Delete buttons on all habits because it
 *  assumes only one profile can use the App.
 */
public class HabitDetailActivity extends AppCompatActivity {
    /** UI buttons */
    private TextView habitDetailTitle, habitDetailStartDate, habitDetailReason, habitDetailDaysToDo, habitDetailProgress, habitDetailVisibility;
    private Button habitDetailDeleteButton, habitDetailEditButton;
    private ProgressBar habitProgressBar;

    /** This is the habit that we need to show the details of in this Activity */
    private Habit habitRecieved;

    /** A reference to the Firestore database */
    private FirebaseFirestore db;

    private ArrayAdapter<String> habitEventsAdapter;

    private float numberOfDaysToDoThisWeek;
    private float numberOfDaysDoneThisWeek;
    private int datesToBeDoneSize;

    private String habitOwnerUsername;

    private int EDITHABIT_REQUEST_CODE = 123;

    private boolean isMyHabit;

    /**
     * This method sets the view and initializes variables. It runs once immediately after entering
     * this Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        // Store a reference to the Firestore database
        db = FirebaseFirestore.getInstance();

        // Enable the back button on the top of the screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Habit that we need to show the details of
        habitRecieved = (Habit) getIntent().getSerializableExtra("habitPassed");

        habitOwnerUsername = SingletonUsername.get(); MainActivity.getInstance().getUsername();
        String isTrue = getIntent().getStringExtra("isMyHabit");
        if (isTrue != null && isTrue.equals("true")){
            isMyHabit = true;
        }else{
            isMyHabit = false;
            habitOwnerUsername = getIntent().getStringExtra("habitOwnerUsername");
        }

        // Access UI elements from the layout
        habitDetailTitle = (TextView) findViewById(R.id.habit_detail_title);
        habitDetailStartDate = (TextView) findViewById(R.id.habit_detail_date);
        habitDetailReason = (TextView) findViewById(R.id.habit_detail_reason);
        habitDetailProgress = (TextView) findViewById(R.id.habit_detail_progress);
        habitProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        habitDetailDaysToDo = (TextView) findViewById(R.id.habit_detail_days_to_do);
        habitDetailVisibility = (TextView) findViewById(R.id.habit_detail_visibility);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);
        ListView habitEventsListView = (ListView) findViewById(R.id.habit_events_listview);


        numberOfDaysToDoThisWeek = habitRecieved.getDaysToBeDone().split(",").length;
        numberOfDaysDoneThisWeek = 0;

        db.collection("habits").document(habitRecieved.getUniqueId())
                .addSnapshotListener((doc, error) -> {
                    if (error == null && doc != null && doc.exists()) {
                        Habit habitParsed = Habit.parseFromDoc(doc.getData());
                        if (habitParsed != null) {
                            habitRecieved.setTitle(habitParsed.getHabitTitle());
                            habitRecieved.setDaysToBeDone(habitParsed.getDaysToBeDone());
                            habitRecieved.setReason(habitParsed.getReason());
                            habitRecieved.setPublic(habitParsed.isPublic());

                            // Enter the details of the habit onto the View
                            habitDetailTitle.setText(habitRecieved.getHabitTitle());
                            habitDetailStartDate.setText("Date started: " + habitParsed.getStartDate());
                            habitDetailReason.setText(habitRecieved.getReason());
                            habitDetailDaysToDo.setText(habitRecieved.getDaysToBeDone());
                            habitDetailVisibility.setText(
                                    habitRecieved.isPublic()
                                            ? ("All followers of " + habitOwnerUsername)
                                            : ("Only " + habitOwnerUsername)
                            );

                            updateProgressBar(habitRecieved.getDaysToBeDone(), habitRecieved.getHabitEvents());
                        } else {
                            Toast.makeText(HabitDetailActivity.this, "This is an invalid Habit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Connect the habit titles data list to the user-interface with an ArrayAdapter
        ArrayList<String> habitEventsCompletionDateList = new ArrayList<>();
        habitEventsAdapter = new ArrayAdapter<>(this, R.layout.habitevents_content, R.id.list_item, habitEventsCompletionDateList);
        habitEventsListView.setAdapter(habitEventsAdapter);

        db.collection("habitEvents").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            habitRecieved.clearHabitEvents();
            habitEventsCompletionDateList.clear();
            for (QueryDocumentSnapshot doc : value) {
                HabitEvent habitEventParsed = HabitEvent.parseFromDoc(doc.getData());
                // Add a guard in case a wrongly structured Habit data is put into firestore
                if (habitEventParsed == null) {
                    continue;
                }
                // Only add if this HabitEvent belongs to the correct Habit in question.
                if (habitEventParsed.getParentHabitUniqueId().equals(habitRecieved.getUniqueId())) {
                    habitRecieved.addToHabitEvents(habitEventParsed);
                    // Set the content to be shown for each habitEvent
                    String dateAndTime = habitEventParsed.getTimeStamp();
                    habitEventsCompletionDateList.add("Completed on: " + dateAndTime);
                }
            }
            habitEventsAdapter.notifyDataSetChanged();
            updateProgressBar(habitRecieved.getDaysToBeDone(), habitRecieved.getHabitEvents());

            //Dynamically update height
            ViewGroup.LayoutParams params = habitEventsListView.getLayoutParams();
            float dpFactor = getApplicationContext().getResources().getDisplayMetrics().density;
            int dpPerItem = 51;
            int offsetInDp = 80;
            int nHabitEvents = habitEventsCompletionDateList.size();
            int height = (int) ((nHabitEvents * dpPerItem + offsetInDp) * dpFactor);
            params.height = nHabitEvents == 0 ? 0 : height;
            habitEventsListView.setLayoutParams(params);
            habitEventsListView.requestLayout();
        });


        // Add listener that navigates to the correct Habit detail page when a habit is clicked
        habitEventsListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(HabitDetailActivity.this, HabitEventDetailActivity.class);
            intent.putExtra("habitEventPassed", habitRecieved.getHabitEvents().get(position));
            startActivity(intent);
        });

        if (isMyHabit) {
            // Show the Edit and Delete buttons
            habitDetailDeleteButton.setVisibility(View.VISIBLE);
            habitDetailEditButton.setVisibility(View.VISIBLE);
            findViewById(R.id.completed_button).setVisibility(View.VISIBLE);
            findViewById(R.id.textView23).setVisibility(View.VISIBLE);
            findViewById(R.id.habit_events_listview).setVisibility(View.VISIBLE);
        }
    }

    /**
     * This simply enables the Back button at the top of screen to go back to the previous Activity
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * This method is called when the Delete button on the activity_habit_detail.xml page is clicked
     * It deletes the habit directly from the Firestore database
     * @param view
     */
    public void DeleteHabit(View view){
        // Delete this habit
        db.collection("habits")
                .document(habitRecieved.getUniqueId())
                .delete();
        // Delete all habitEvents associated with this habit
        db.collection("habitEvents")
                .whereEqualTo("parentHabitUniqueId", habitRecieved.getUniqueId())
                .get()
                .addOnSuccessListener((value) -> {
                    for (QueryDocumentSnapshot doc : value) {
                        doc.getReference().delete();
                    }
                });
        finish();
    }

    /**
     * This method is called when the Edit button on the activity_habit_detail.xml page is clicked
     * It navigates to the HabitEditActivity page for the given habit
     * @param view
     */
    public void EditHabit(View view) {
        Intent intent = new Intent(this, HabitEditActivity.class);
        intent.putExtra("habitPassedIn", (Serializable) habitRecieved);
        startActivityForResult(intent, EDITHABIT_REQUEST_CODE);
    }

    public void onClickCompleted(View view) {
        Intent intent = new Intent(this, AddHabitEventActivity.class);
        intent.putExtra("habit", habitRecieved);
        startActivity(intent);
    }

    private void updateProgressBar(String daysToBeDone, ArrayList<HabitEvent> habitEvents) {
        ArrayList<String> datesOfThisWeek = datesOfThisWeek(daysToBeDone);

        numberOfDaysToDoThisWeek = datesOfThisWeek.size();
        numberOfDaysDoneThisWeek = 0;

        for (HabitEvent habitEvent : habitEvents) {
            datesOfThisWeek.remove(habitEvent.getDateString());
        }

        if (numberOfDaysToDoThisWeek != 0) {
            numberOfDaysDoneThisWeek = numberOfDaysToDoThisWeek - datesOfThisWeek.size();
            float percentage = (numberOfDaysDoneThisWeek / numberOfDaysToDoThisWeek) * 100;
            // prevent percentage from going over 100%
            percentage = Math.min(percentage, 100);
            String percentString = String.format("%.2f", percentage);

            // Update User interface
            habitDetailProgress.setText(percentString + "%");
            habitProgressBar.setProgress((int) percentage);
        }
    }

    private ArrayList<String> datesOfThisWeek(String daysToBeDone) {
        // Split the days of the week into an ArrayList; e.g. ["Monday", "Friday"]
        List<String> dayToBeDoneList = Arrays.asList(habitRecieved.getDaysToBeDone().split(", "));
        ArrayList<String> daysOfTheWeek = new ArrayList(dayToBeDoneList);


        // Initialize return value
        ArrayList<String> datesOfTheWeek = new ArrayList<>();

        for (String day : daysOfTheWeek) {

            // Convert the string to a Calendar enum
            int dayNum = 0;
            if (day.equals("Monday")) {
                dayNum = Calendar.MONDAY;
            } else if (day.equals("Tuesday")) {
                dayNum = Calendar.TUESDAY;
            } else if (day.equals("Wednesday")) {
                dayNum = Calendar.WEDNESDAY;
            } else if (day.equals("Thursday")) {
                dayNum = Calendar.THURSDAY;
            } else if (day.equals("Friday")) {
                dayNum = Calendar.FRIDAY;
            } else if (day.equals("Saturday")) {
                dayNum = Calendar.SATURDAY;
            } else if (day.equals("Sunday")) {
                dayNum = Calendar.SUNDAY;
            }

            // Get the date of corresponding day of the week (in the current week)
            // Source: https://stackoverflow.com/a/9307961/8270982
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Calendar.DAY_OF_WEEK, dayNum);
            String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());

            // Add to array
            datesOfTheWeek.add(dateString);
        }

        // Return the array of dates needed
        return datesOfTheWeek;
    }
}