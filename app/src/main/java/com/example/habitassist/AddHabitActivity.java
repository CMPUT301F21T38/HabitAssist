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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
    private boolean isPublic;
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
        isPublic = true;
    }

    /**
     * This method is called when the Save button on the activity_add_habit.xml page is clicked
     * It adds a new habit directly to the Firestore database if the input fields are valid.
     * @param view
     */
    public void SaveButton(View view){
        String titleAdded = ((EditText) findViewById(R.id.comment_edit_text)).getText().toString();
        String reasonAdded = ((EditText) findViewById(R.id.editTextTextPersonName2)).getText().toString();

        if (titleAdded.length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the title under 20 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (titleAdded.isEmpty()) {
            Toast.makeText(this, "Title of the habit cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (reasonAdded.length() > 30) {
            Toast.makeText(getApplicationContext(), "Please keep the reason under 30 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> dayToBeDoneArray = new ArrayList<>();
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
        String daysToBeDone = TextUtils.join(", ", dayToBeDoneArray);
        String date_Started = Habit.dateStringFromDatePicker((DatePicker) findViewById(R.id.editTextDate2));

        Habit habit = new Habit(titleAdded, reasonAdded, date_Started, daysToBeDone,
                SingletonUsername.get(), isPublic);
        db.collection("habits").document(habit.getUniqueId()).set(habit.getDocument());
        finish();
    }

    /**
     * This method is called when the Cancel button on the activity_add_habit.xml page is clicked
     * @param view
     */
    public void CancelButton(View view){
        // Exit the activity
        finish();
    }

    /**
     * This method is called when the Public button is pushed on the activity_add_habit.xml page.
     * It sets a habit to public or private meaning that the habit is either viewable(Public) to others
     * or it is not.
     * @param view
     */
    public void isPublicButton(View view){
        if (isPublic) {
            isPublic = false;
           ((TextView) view).setText("Private");
        } else {
            isPublic = true;
            ((TextView) view).setText("Public");
        }

    }
}