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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * The Android Activity that handles displaying the details of a particular habit and deleting it.
 * This Activity also enables navigating to the activity where the habit details can be edited.
 *
 *  Currently outstanding issues: Always shows the Edit and Delete buttons on all habits because it
 *  assumes only one profile can use the App.
 */
public class HabitEventDetailActivity extends AppCompatActivity {
    private static final String TAG = "HabitEventDetail";
    /** UI buttons */
    private TextView habitTitle, habitEventTime, habitEventComment;
    private Button habitDetailDeleteButton, habitDetailEditButton;
    private ImageView habitEventImageView;

    /** This is the habit that we need to show the details of in this Activity */
    private HabitEvent habitEventRecieved;

    /** A reference to the Firestore database */
    private FirebaseFirestore db;

    private ArrayAdapter<String> habitEventsAdapter;

    /**
     * This method sets the view and initializes variables. It runs once immediately after entering
     * this Activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_event_detail);

        // Store a reference to the Firestore database
        db = FirebaseFirestore.getInstance();

        // Enable the back button on the top of the screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the HabitEvent that we need to show the details of
        habitEventRecieved = (HabitEvent) getIntent().getSerializableExtra("habitEventPassed");

        // updateHabitEventRecieved();

        // Access UI elements from the layout
        habitTitle = (TextView) findViewById(R.id.habit_title);
        habitEventTime = (TextView) findViewById(R.id.habit_event_detail_time);
        habitEventComment = (TextView) findViewById(R.id.habit_event_comment);
        habitEventImageView = (ImageView) findViewById(R.id.image_view);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);


        // Enter the details of the habit event onto the View
        habitTitle.setText(habitEventRecieved.getHabitTitle());
        habitEventTime.setText("Date started: " + habitEventRecieved.getTimeStamp());

        // habitEventComment.setText(habitEventRecieved.getComment());
        // String imageBitmapString = habitEventRecieved.getImageBitmapString();
        // if (imageBitmapString != null && !imageBitmapString.isEmpty()) {
        //     Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapString);
        //     habitEventImageView.setImageBitmap(imageBitmap);
        // }

        db.collection("habitEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    String comment = (String) data.get("comment");
                    String habitTitle = (String) data.get("habitTitle");
                    String timeStamp = (String) data.get("timeStamp");
                    String imageBitmapString = (String) data.get("imageBitmapString");
                    // Add a guard in case a wrongly structured Habit data is put into firestore
                    if (habitTitle == null || timeStamp == null) {
                        continue;
                    }
                    // Only add if this HabitEvent belongs to the correct Habit in question.
                    if (habitEventRecieved.getUniqueId().equals(doc.getId())) {
                        habitEventComment.setText(comment);
                        if (imageBitmapString != null && !imageBitmapString.isEmpty()) {
                            Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapString);
                            habitEventImageView.setImageBitmap(imageBitmap);
                        }
                        habitEventRecieved = new HabitEvent(habitTitle, timeStamp, comment, imageBitmapString);
                    }
                }
            }
        });

        // // TODO: Determine here if this habit actually belongs to the correct profile in database
        // if (true) {
        //     // Show the Edit and Delete buttons
        //     habitDetailDeleteButton.setVisibility(View.VISIBLE);
        //     habitDetailEditButton.setVisibility(View.VISIBLE);
        // }
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
        // updateHabitEventRecieved();
        String habitEventId = habitEventRecieved.getUniqueId();
        db.collection("habitEvents")
                .document(habitEventId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HabitEventDetailActivity.this, habitEventId+" successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HabitEventDetailActivity.this, "Error deleting "+habitEventId +" :" + e, Toast.LENGTH_SHORT).show();
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
        // updateHabitEventRecieved();
        Intent intent = new Intent(this, HabitEventEditActivity.class);
        intent.putExtra("habitEventPassed", habitEventRecieved);
        startActivity(intent);
    }
}