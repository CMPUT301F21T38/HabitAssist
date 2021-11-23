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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
    private TextView habitDetailTitle, habitDetailStartDate, habitDetailReason, habitDetailDaysToDo, habitDetailProgress;
    private Button habitDetailDeleteButton, habitDetailEditButton;
    private ProgressBar habitProgressBar;

    /** This is the habit that we need to show the details of in this Activity */
    private Habit habitRecieved;

    /** A reference to the Firestore database */
    private FirebaseFirestore db;

    private ArrayAdapter<String> habitEventsAdapter;

    private float numberOfDaysToDoThisWeek;
    private float numberOfDaysDoneThisWeek;

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

        // Access UI elements from the layout
        habitDetailTitle = (TextView) findViewById(R.id.habit_detail_title);
        habitDetailStartDate = (TextView) findViewById(R.id.habit_detail_date);
        habitDetailReason = (TextView) findViewById(R.id.habit_detail_reason);
        habitDetailProgress = (TextView) findViewById(R.id.habit_detail_progress);
        habitProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        habitDetailDaysToDo = (TextView) findViewById(R.id.habit_detail_days_to_do);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);
        ListView habitEventsListView = (ListView) findViewById(R.id.habit_events_listview);


        String[] daysToBeDone = habitRecieved.getDaysToBeDone().split(",");
        numberOfDaysToDoThisWeek = daysToBeDone.length;
        numberOfDaysDoneThisWeek = 0;
        // // Avoid zero division error
        // if (numberOfDaysToDoThisWeek != 0) {
        //     float percentage = (numberOfDaysDoneThisWeek / numberOfDaysToDoThisWeek) * 100;
        //     String percentString = String.format("%.2f", percentage);
        //     // Update User interface
        //     habitDetailProgress.setText(percentString + "%");
        // }

        db.collection("habits").document(habitRecieved.getHabitTitle())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException error) {
                        if (error == null && doc != null && doc.exists()) {
                            Map<String, Object> data = doc.getData();
                            String title = (String) data.get("title");
                            String reason = (String) data.get("reason");
                            String startDate = (String) data.get("startDate");
                            String daysToBeDone = (String) data.get("daysToBeDone");

                            if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                                habitRecieved = new Habit(title, reason, startDate, daysToBeDone);
                            }

                            if (startDate == null) {
                                startDate = "";
                            }

                            // Enter the details of the habit onto the View
                            habitDetailTitle.setText(title);
                            habitDetailStartDate.setText("Date started: " + startDate);
                            habitDetailReason.setText(reason);
                            habitDetailDaysToDo.setText(daysToBeDone);
                        }
                    }
                });

        ArrayList<HabitEvent> habitEventsList = new ArrayList<>();
        ArrayList<String> habitEventsIdList = new ArrayList<>();
        db.collection("habitEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // Split the days of the week into an ArrayList; e.g. ["Monday", "Friday"]
                List<String> dayToBeDoneList = Arrays.asList(habitRecieved.getDaysToBeDone().split(", "));
                ArrayList dayToBeDoneArray = new ArrayList(dayToBeDoneList);

                // Convert the days into dates in current week
                // e.g. ["Monday", "Friday"] -> ["2021-11-22", "2021-11-26"]
                ArrayList<String> datesToBeDone = datesOfThisWeek(dayToBeDoneArray);
                numberOfDaysDoneThisWeek = 0;

                habitEventsList.clear();
                habitEventsIdList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    String comment = (String) data.get("comment");
                    String habitTitle = (String) data.get("habitTitle");
                    String timeStamp = (String) data.get("timeStamp");
                    String dateString = (String) data.get("dateString");
                    String imageBitmapString = (String) data.get("imageBitmapString");
                    // Add a guard in case a wrongly structured Habit data is put into firestore
                    if (habitTitle == null || timeStamp == null) {
                        continue;
                    }
                    // Only add if this HabitEvent belongs to the correct Habit in question.
                    if (habitTitle.equals(habitRecieved.getHabitTitle())) {
                        HabitEvent habitEvent = new HabitEvent(habitTitle, timeStamp, comment, imageBitmapString);
                        habitEventsList.add(habitEvent);
                        // Set the content to be shown for each habitEvent
                        String dateAndTime = doc.getId().split("\\*")[1];
                        habitEventsIdList.add("Completed on: " + dateAndTime);

                        datesToBeDone.remove(dateString);
                    }
                }
                habitEventsAdapter.notifyDataSetChanged();

                // Avoid zero division error
                if (numberOfDaysToDoThisWeek != 0) {
                    numberOfDaysDoneThisWeek = numberOfDaysToDoThisWeek - datesToBeDone.size();
                    float percentage = (numberOfDaysDoneThisWeek / numberOfDaysToDoThisWeek) * 100;
                    // prevent percentage from going over 100%
                    percentage = Math.min(percentage, 100);
                    String percentString = String.format("%.2f", percentage);

                    // Update User interface
                    habitDetailProgress.setText(percentString + "%");
                    habitProgressBar.setProgress((int) percentage);
                }

                // updateHabitProgress();
            }
        });

        // Connect the habit titles data list to the user-interface with an ArrayAdapter
        habitEventsAdapter = new ArrayAdapter<>(this, R.layout.habitevents_content, R.id.list_item, habitEventsIdList);
        habitEventsListView.setAdapter(habitEventsAdapter);

        // Add listener that navigates to the correct Habit detail page when a habit is clicked
        habitEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(HabitDetailActivity.this, HabitEventDetailActivity.class);
                intent.putExtra("habitEventPassed", habitEventsList.get(position));
                startActivity(intent);
            }
        });



        // // Enter the details of the habit onto the View
        // habitDetailTitle.setText(habitRecieved.getHabitTitle());
        // habitDetailStartDate.setText("Date started: " + habitRecieved.getStartDate());
        // habitDetailReason.setText(habitRecieved.getReason());
        // habitDetailDaysToDo.setText(habitRecieved.getDaysToBeDone());

        // TODO: Determine here if this habit actually belongs to the correct profile in database
        if (true) {
            // Show the Edit and Delete buttons
            habitDetailDeleteButton.setVisibility(View.VISIBLE);
            habitDetailEditButton.setVisibility(View.VISIBLE);
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
        db.collection("habits")
                .document(habitRecieved.getHabitTitle())
                .delete();
        db.collection("habitEvents")
                .whereEqualTo("habitTitle", habitRecieved.getHabitTitle())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        for (QueryDocumentSnapshot doc : value) {
                            doc.getReference().delete();
                        }
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
        // Get mainActivity
        MainActivity mainActivityInstance = MainActivity.getInstance();
        // Set the habit to be edited into the DeleteAndEdit variable in the MainActivity
        mainActivityInstance.DeleteAndEdit = habitRecieved.getHabitTitle();
        // Let mainActivity handle the deleting
        mainActivityInstance.EditHabit(view);
        finish();
    }

    public void onClickCompleted(View view) {
        Intent intent = new Intent(this, AddHabitEventActivity.class);
        intent.putExtra("habit", habitRecieved);
        startActivity(intent);
    }

    private void updateHabitProgress() {
        // Split the days of the week into an ArrayList; e.g. ["Monday", "Friday"]
        List<String> dayToBeDoneList = Arrays.asList(habitRecieved.getDaysToBeDone().split(", "));
        ArrayList dayToBeDoneArray = new ArrayList(dayToBeDoneList);

        // Convert the days into dates in current week
        // e.g. ["Monday", "Friday"] -> ["2021-11-22", "2021-11-26"]
        ArrayList<String> datesToBeDone = datesOfThisWeek(dayToBeDoneArray);

        numberOfDaysDoneThisWeek = 0;

        // Do for each date
        for (String dateString : datesToBeDone) {

            // Search for a habitEvent (of correct habit) that was completed on said date
            db.collection("habitEvents")
                    .whereEqualTo("habitTitle", habitRecieved.getHabitTitle())
                    .whereEqualTo("dateString", dateString)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot value) {
                            for (QueryDocumentSnapshot doc : value) {
                                // If such a habitEvent was found then increment this count
                                numberOfDaysDoneThisWeek += 1;
                            }

                            // Avoid zero division error
                            if (numberOfDaysToDoThisWeek != 0) {
                                float percentage = (numberOfDaysDoneThisWeek / numberOfDaysToDoThisWeek) * 100;
                                // prevent percentage from going over 100%
                                percentage = Math.max(percentage, 100);
                                String percentString = String.format("%.2f", percentage);

                                // Update User interface
                                habitDetailProgress.setText(percentString + "%");
                            }
                        }
                    });
        }
    }

    private ArrayList<String> datesOfThisWeek(ArrayList<String> daysOfTheWeek) {
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
            cal.set(Calendar.DAY_OF_WEEK, dayNum);
            String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());

            // Add to array
            datesOfTheWeek.add(dateString);
        }

        // Return the array of dates needed
        return datesOfTheWeek;
    }
}