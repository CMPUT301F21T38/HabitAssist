/*Copyright [2021] [CMPUT301F21T38]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
    
package com.example.habitassist;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton myFab;
    public ArrayList<Habit> habitList;
    ArrayAdapter<String> habitAdapter;
    ArrayList<String> habitList2;
    private ListView listview;

    public FirebaseFirestore db;

    final String TAG = "MainActivity";

    // This variable is set immediately after a ListView of habits is clicked
    // It stores the title of the habit clicked. The unique title is used to identify which habit
    // object to delete or edit if an action like that is triggered.
    public String DeleteAndEdit;

    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        listview = (ListView) findViewById(R.id.listview);
        habitList = new ArrayList<>();
        habitList2 = new ArrayList<>();

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
        CollectionReference habitsCollection = db.collection("habits");
        habitsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                habitList.clear();
                habitList2.clear();
                for (QueryDocumentSnapshot doc: value) {
                    Map<String, Object> data = doc.getData();
                    String title = (String) data.get("title");
                    String reason = (String) data.get("reason");
                    String startDate = (String) data.get("startDate");
                    String daysToBeDone = (String) data.get("daysToBeDone");
                    if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                        Habit habit = new Habit(title, reason, startDate, daysToBeDone);
                        habitList.add(habit);
                        if (habit.isForToday()) {
                            habitList2.add(title);
                        }
                    }
                }
                habitAdapter.notifyDataSetChanged();
            }
        });
        habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, habitList2);
        listview.setAdapter(habitAdapter);

        myFab = (FloatingActionButton) findViewById(R.id.add_habit_button);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddMainHabit();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // it returns the name of the habit from the Listview OR the ArrayList
                DeleteAndEdit = habitList2.get(i);
                Intent intent3 = new Intent(MainActivity.this, HabitDetailActivity.class);
                String uniqueTitle = habitList2.get(i);
                Habit habitPassed = habitList.get(i); // Not necessarily the correct Habit object
                for (Habit habit : habitList) {
                    if (habit.getHabitTitle().equals(uniqueTitle)) {
                        habitPassed = habit;
                        break;
                    }
                }
                intent3.putExtra("habitPassed", habitPassed);
                startActivityForResult(intent3, 5);

            }
        });
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void DeleteHabit(View view){
        db.collection("habits").document(DeleteAndEdit)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void EditHabit(View view) {
        System.out.println("EditHabit entered");
        Intent intent4= new Intent(this, HabitEditActivity.class);

        // Look for the habit with the same title as DeleteAndEdit
        Habit habitPassedIn = null;
        for (Habit habit : habitList) {
            if (habit.getHabitTitle().equals(DeleteAndEdit)) {
                habitPassedIn = habit;
                break;
            }
        }
        if (habitPassedIn != null) {
            intent4.putExtra("habitPassedIn", (Serializable) habitPassedIn);
            startActivityForResult(intent4, 9);
        } else {
            Toast.makeText(getApplicationContext(), "DeleteAndEdit is: " + DeleteAndEdit + " " +"HabitPassedIn is NULL", Toast.LENGTH_SHORT).show();
        }
    }

    public void AddMainHabit(){
        System.out.println("AddMainHabit entered1");
        Intent intent1 = new Intent(this, AddHabitActivity.class);
        startActivityForResult(intent1, 3);
    }

    public void ProfileButton(View view){
        Intent intent2 = new Intent(this, ProfileActivity.class);
        intent2.putExtra("habit2", habitList);
        startActivityForResult(intent2, 4);
    }

    public void FeedButton(View view){

    }
    public void HomeButton(View view){
        // do something when the home button is clicked
    }
}
