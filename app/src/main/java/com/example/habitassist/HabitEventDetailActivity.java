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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.android.gms.maps.SupportMapFragment;


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

    private Map mapController;

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

        // Create a map controller object
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        View mapViewWidget = findViewById(R.id.google_map);
        mapController = new Map(supportMapFragment, mapViewWidget);

        // Access UI elements from the layout
        habitTitle = (TextView) findViewById(R.id.habit_title);
        habitEventTime = (TextView) findViewById(R.id.habit_event_detail_time);
        habitEventComment = (TextView) findViewById(R.id.habit_event_comment);
        habitEventImageView = (ImageView) findViewById(R.id.image_view);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);


        // Enter the details of the habit event onto the View
        db.collection("habits")
                .document(habitEventRecieved.getParentHabitUniqueId())
                .get()
                .addOnSuccessListener((doc) -> {
                    habitTitle.setText(Habit.parseFromDoc(doc.getData()).getHabitTitle());
                });

        db.collection("habitEvents")
                .document(habitEventRecieved.getUniqueId())
                .addSnapshotListener((doc, error) -> {
                    if (error == null && doc != null && doc.exists()) {
                        HabitEvent habitEvent = HabitEvent.parseFromDoc(doc.getData());
                        if (habitEvent != null) {
                            String comment = habitEvent.getComment();
                            if (comment != null & !comment.isEmpty()) {
                                findViewById(R.id.comment_text).setVisibility(View.VISIBLE);
                                habitEventComment.setVisibility(View.VISIBLE);
                                habitEventComment.setText(habitEvent.getComment());
                            } else {
                                findViewById(R.id.comment_text).setVisibility(View.GONE);
                                habitEventComment.setVisibility(View.GONE);
                            }
                            String imageBitmapString = habitEvent.getImageBitmapString();
                            if (imageBitmapString != null && !imageBitmapString.isEmpty()) {
                                findViewById(R.id.picture_text).setVisibility(View.VISIBLE);
                                findViewById(R.id.image_view).setVisibility(View.VISIBLE);

                                Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapString);
                                habitEventImageView.setImageBitmap(imageBitmap);
                            } else {
                                findViewById(R.id.picture_text).setVisibility(View.GONE);
                                findViewById(R.id.image_view).setVisibility(View.GONE);
                            }
                            String latlng = habitEvent.getLatlngString();
                            if (latlng != null && !latlng.isEmpty()) {
                                findViewById(R.id.location_text).setVisibility(View.VISIBLE);
                                mapController.showOnMap(latlng);
                            } else {
                                findViewById(R.id.location_text).setVisibility(View.GONE);
                                mapController.hideMapWidget();
                            }
                            habitEventRecieved = habitEvent;
                        }
                    }
                });
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
     * This method is called when the Delete button on the activity_habit_event_detail.xml page is clicked
     * It deletes the habitEvent directly from the Firestore database
     * @param view
     */
    public void DeleteHabitEvent(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Deleting a HabitEvent")
                .setMessage("Are you sure you want to delete this habit event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String habitEventId = habitEventRecieved.getUniqueId();
                    db.collection("habitEvents")
                            .document(habitEventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(HabitEventDetailActivity.this,
                                        habitEventId+" successfully deleted!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(HabitEventDetailActivity.this,
                                        "Error deleting "+habitEventId+" :" + e, Toast.LENGTH_SHORT).show();
                            });
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * This method is called when the Edit button on the activity_habit_detail.xml page is clicked
     * It navigates to the HabitEditActivity page for the given habit
     * @param view
     */
    public void EditHabitEvent(View view) {
        Intent intent = new Intent(this, HabitEventEditActivity.class);
        intent.putExtra("habitEventPassed", habitEventRecieved);
        startActivity(intent);
    }
}