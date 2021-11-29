package com.example.habitassist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    /** A list of the habits belonging to the profile that needs to be done today */
    private ArrayList<Habit> habitList;
    /** A list of the titles of the habits belonging to the profile that needs to be done today */
    private ArrayList<String> habitTitleList;
    /** An array adapter used to show the titles in habitTitleList into a ListView */
    private ArrayAdapter<String> habitAdapter;

    /** A reference to the Firestore database  */
    public FirebaseFirestore db;

    /** A name tag used in logging statements from this activity */
    final String TAG = "MainActivity";

    /**
     * This variable is set immediately after a ListView of habits is clicked. It stores the title
     * of the habit clicked. The unique title is used to identify which habit object to delete or
     * edit if an action like that is triggered. */
    public String DeleteAndEdit;


    int profileCode = 42;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set username
        username = SingletonUsername.get();
        // username = MainActivity.getInstance().getUsername();

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize variables
        ListView habitsListView = (ListView) findViewById(R.id.listview);
        habitList = new ArrayList<>();
        habitTitleList = new ArrayList<>();

        // Add listener that reacts to changes to the Firestore database and updates the local UI
        db.collection("habits")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value.isEmpty()) {
                        return;
                    }
                    habitList.clear();
                    habitTitleList.clear();
                    for (QueryDocumentSnapshot doc: value) {
                        Habit habit = Habit.parseFromDoc(doc.getData());
                        if (habit != null && habit.getOwnerUsername() != null && habit.getOwnerUsername().equals(username) && habit.isForToday()) {
                            habitList.add(habit);
                            habitTitleList.add(habit.getHabitTitle());
                        }
                    }
                    habitAdapter.notifyDataSetChanged();
                });

        // Connect the habit titles data list to the user-interface with an ArrayAdapter
        habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, habitTitleList);
        habitsListView.setAdapter(habitAdapter);

        // Add listener that navigates to the correct Habit detail page when a habit is clicked
        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // it returns the name of the habit from the Listview OR the ArrayList
                Intent intent = new Intent(HomeActivity.this, HabitDetailActivity.class);
                Habit habitPassed = habitList.get(i);
                intent.putExtra("habitPassed", habitPassed);
                intent.putExtra("isMyHabit", "true");
                startActivity(intent);
            }
        });
    }

    // Button onclick handlers

    public void newHabitButtonHandler(View view) {
        Intent intent = new Intent(this, AddHabitActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the Feed button on the activity_main.xml page is clicked
     * @param view
     */
    public void FeedButton(View view){
        Intent intent = new Intent(this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }

    public void ProfileButton(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }

    @Override
    public void onBackPressed() {
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
    }
}