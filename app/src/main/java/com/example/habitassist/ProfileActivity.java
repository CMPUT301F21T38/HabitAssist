package com.example.habitassist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileActivity extends AppCompatActivity {
    private Intent p_intent;
    private ListView p_list;
    ArrayList<Habit> profile_habitList;
    ArrayList<String> p_habit_titles;
    int position;

    ArrayAdapter<String> profile_habitAdapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        p_intent = getIntent();
        p_list = (ListView) findViewById(R.id.profile_listview);

        profile_habitList = new ArrayList<>();
        p_habit_titles = new ArrayList<>();

        // Add an listener that reacts to changes to the Firestore database and updates the local UI
        db = MainActivity.getInstance().db;
        db.collection("habits").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                profile_habitList.clear();
                p_habit_titles.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    String title = (String) data.get("title");
                    String reason = (String) data.get("reason");
                    String startDate = (String) data.get("startDate");
                    String daysToBeDone = (String) data.get("daysToBeDone");
                    // Add a guard in case a wrongly structured Habit data is put into firestore
                    if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                        Habit habit = new Habit(title, reason, startDate, daysToBeDone);
                        profile_habitList.add(habit);
                        p_habit_titles.add(title);
                    }
                }
                profile_habitAdapter.notifyDataSetChanged();
            }
        });

        profile_habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, p_habit_titles);
        p_list.setAdapter(profile_habitAdapter);

        p_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // it returns the index of the name of the medicine from the Listview OR the ArrayList
                position = i;
                Intent intent3 = new Intent(ProfileActivity.this, HabitDetailActivity.class);
                Habit tempo_passed = profile_habitList.get(position);
                intent3.putExtra("habitPassed", tempo_passed);
                startActivityForResult(intent3, 5);

            }
        });
    }

    public void addHabitButtonClickHandler(View view) {
        startActivity(new Intent(ProfileActivity.this, AddHabitActivity.class));
    }

    public void FeedButton(View view) {

    }

    public void HomeButton(View view) {
        finish();
    }
}