package com.example.habitassist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private Intent p_intent;
    private ListView p_list;
    ArrayList<Habit> profile_habitList;
    ArrayList<String> p_habit_titles;
    int position;

    ArrayAdapter<String> profile_habitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        p_intent = getIntent();
        p_list = (ListView) findViewById(R.id.profile_listview);

        p_habit_titles = new ArrayList<>();


        profile_habitList = (ArrayList<Habit>) p_intent.getSerializableExtra("habit2");

        for(int j = 0; j<profile_habitList.size(); j++){
            Habit habit_tempo = profile_habitList.get(j);
            String title_tempo = habit_tempo.getHabitTitle();
            p_habit_titles.add(title_tempo);
        }
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

    public void FeedButton(View view){

    }
    public void HomeButton(View view){
        finish();
    }
}