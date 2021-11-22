/*
 * This file implements the Android Activity called HabitEditActivity
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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Android Activity that handles getting information about a Habit from the user, validating
 * it and then updating it to the database of habits. It pre-fills the user interface fields with
 * the details of the particular Habit being edited.
 */
public class HabitEditActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_edit_habit);

        // Store a reference to cloud database
        db = FirebaseFirestore.getInstance();

        // Get the Habit to edit that was passed in from MainActivity
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassedIn");

        // get the edittext and datepicker objects
        EditText title = (EditText) findViewById(R.id.comment_edit_text);
        EditText reason = (EditText) findViewById(R.id.editTextTextPersonName2);

        // set the habit values that have already been given previously
        title.setText(habit.getHabitTitle());
        reason.setText(habit.getReason());

        // check day boxes that have already been checked
        String[] days = habit.getDaysToBeDone().split(",");

        if (Arrays.asList(days).contains("Monday")) {
            CheckBox monday = findViewById(R.id.checkbox_Monday);
            monday.setChecked(true);
        }

        if (Arrays.asList(days).contains(" Tuesday")) {
            CheckBox tuesday = findViewById(R.id.checkbox_Tuesday);
            tuesday.setChecked(true);
        }

        if (Arrays.asList(days).contains(" Wednesday")) {
            CheckBox wed = findViewById(R.id.checkbox_Wednesday);
            wed.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Thursday")) {
            CheckBox thurs = findViewById(R.id.checkbox_Thursday);
            thurs.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Friday")) {
            CheckBox fri = findViewById(R.id.checkbox_Friday);
            fri.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Saturday")) {
            CheckBox sat = findViewById(R.id.checkbox_Saturday);
            sat.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Sunday")) {
            CheckBox sun = findViewById(R.id.checkbox_Sunday);
            sun.setChecked(true);
        }

    }

    /**
     * This method is called when the Save button on the activity_edit_habit.xml page is clicked
     * It updates the habit directly in the Firestore database if the input fields are valid.
     * @param view
     */
    public void SaveButton(View view){
        ArrayList<String> HabitDays = new ArrayList<>();

        // get the habit to edit passed in from MainActivity
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassedIn");

        // get the edittext and datepicker objects
        EditText title_added = (EditText) findViewById(R.id.comment_edit_text);
        EditText reason_added = (EditText) findViewById(R.id.editTextTextPersonName2);
        //DatePicker take_date = (DatePicker) findViewById(R.id.editTextDate2);

        // get the title and reason from the user
        String main_title = title_added.getText().toString();
        String main_reason = reason_added.getText().toString();

        if (((CheckBox) findViewById(R.id.checkbox_Monday)).isChecked()) {
            HabitDays.add("Monday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Tuesday)).isChecked()) {
            HabitDays.add("Tuesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Wednesday)).isChecked()) {
            HabitDays.add("Wednesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Thursday)).isChecked()) {
            HabitDays.add("Thursday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Friday)).isChecked()) {
            HabitDays.add("Friday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Saturday)).isChecked()) {
            HabitDays.add("Saturday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Sunday)).isChecked()) {
            HabitDays.add("Sunday");
        }

        // edit the habit object
        habit.setTitle(main_title);
        habit.setReason(main_reason);
        //habit.setStartDate(date_Started);
        habit.setDaysToBeDone(TextUtils.join(", ", HabitDays));


        if (title_added.getText().toString().length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the title under 20 characters", Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() > 30) {
            Toast.makeText(getApplicationContext(), "Please keep the reason under 30 characters", Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() <= 30 && title_added.getText().toString().length() <= 20) {
            // Directly Edit the Firestore entry from here instead of passing it back to the MainActivity
            String DeleteAndEdit = MainActivity.getInstance().DeleteAndEdit;
            db.collection("habits").document(DeleteAndEdit).delete();
            db.collection("habits").document(habit.getHabitTitle()).set(habit.getDocument());

            // Exit the activity
            finish();
        }


    }

    /**
     * This method is called when the Cancel button on the activity_edit_habit.xml page is clicked
     * @param view
     */
    public void CancelButton(View view){
        // Exit the activity
        finish();
    }
}
