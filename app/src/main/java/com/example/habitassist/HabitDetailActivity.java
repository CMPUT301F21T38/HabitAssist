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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The Android Activity that handles displaying the details of a particular habit and deleting it.
 * This Activity also enables navigating to the activity where the habit details can be edited.
 *
 *  Currently outstanding issues: Always shows the Edit and Delete buttons on all habits because it
 *  assumes only one profile can use the App.
 */
public class HabitDetailActivity extends AppCompatActivity {
    /** UI buttons */
    private TextView habitDetailTitle, habitDetailStartDate, habitDetailReason, habitDetailDaysToDo;
    private Button habitDetailDeleteButton, habitDetailEditButton;

    /** This is the habit that we need to show the details of in this Activity */
    private Habit habitRecieved;

    /** A reference to the Firestore database */
    private FirebaseFirestore db;

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
        habitDetailDaysToDo = (TextView) findViewById(R.id.habit_detail_days_to_do);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);

        // Enter the details of the habit onto the View
        habitDetailTitle.setText(habitRecieved.getHabitTitle());
        habitDetailStartDate.setText("Date started: " + habitRecieved.getStartDate());
        habitDetailReason.setText(habitRecieved.getReason());
        habitDetailDaysToDo.setText(habitRecieved.getDaysToBeDone());

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


}