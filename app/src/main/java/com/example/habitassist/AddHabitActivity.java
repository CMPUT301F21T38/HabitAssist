/*
 * This file implements the Android Activity called AddHabitActivity
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Android Activity that handles getting information about a new Habit from the user, validating
 * it and then adding it to the database of habits.
 */
public class AddHabitActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_habit);

        // Store a reference to the Firestore database
        db = FirebaseFirestore.getInstance();
    }

    /**
     * This method is called when the Save button on the activity_add_habit.xml page is clicked
     * It adds a new habit directly to the Firestore database if the input fields are valid.
     * @param view
     */
    public void SaveButton(View view){
        ArrayList<String> dayToBeDoneArray = new ArrayList<>();
        EditText title_added = (EditText) findViewById(R.id.editTextTextPersonName);

        EditText reason_added = (EditText) findViewById(R.id.editTextTextPersonName2);

        DatePicker take_date = (DatePicker) findViewById(R.id.editTextDate2);
        int take_day = take_date.getDayOfMonth();
        String take_day1 = Integer.toString(take_day);
        if (take_day < 10){
            take_day1 = "0" + take_day1;
        }
        int take_month = take_date.getMonth() + 1;
        int take_year = take_date.getYear();

        String take_month1 = Integer.toString(take_month);
        if (take_month < 10){
            take_month1 = "0" + take_month1;
        }
        String take_year1 = Integer.toString(take_year);
        String date_Started = take_year1 + "-" + take_month1 + "-" + take_day1;

        if (((CheckBox) findViewById(R.id.checkbox_Monday)).isChecked()) {
            dayToBeDoneArray.add("Monday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Tuesday)).isChecked()) {
            dayToBeDoneArray.add("Tuesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Wednesday)).isChecked()) {
            dayToBeDoneArray.add("Wednesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Thursday)).isChecked()) {
            dayToBeDoneArray.add("Thursday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Friday)).isChecked()) {
            dayToBeDoneArray.add("Friday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Saturday)).isChecked()) {
            dayToBeDoneArray.add("Saturday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Sunday)).isChecked()) {
            dayToBeDoneArray.add("Sunday");
        }

        if (title_added.getText().toString().length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the title under 20 characters", Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() > 30) {
            Toast.makeText(getApplicationContext(), "Please keep the reason under 30 characters", Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() <= 30 && title_added.getText().toString().length() <= 20) {
            Habit habit1 = new Habit(title_added.getText().toString(), reason_added.getText().toString(), date_Started, TextUtils.join(", ", dayToBeDoneArray));

            String title = habit1.getHabitTitle();
            HashMap<String, String> habitDocument = habit1.getDocument();
            db.collection("habits").document(title).set(habitDocument);
            finish();
        }
    }

    /**
     * This method is called when the Cancel button on the activity_add_habit.xml page is clicked
     * @param view
     */
    public void CancelButton(View view){
        // Exit the activity
        finish();
    }
}