package com.example.habitassist;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HabitEditActivity extends AppCompatActivity {
    private TextView habitTitle, habitStartDate, habitReason, habitDaysToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassedIn");

        // Basically we are just doing the same thing as the add activity but in reverse.
        // If we hit save then update habit cancel means to make no change and return to normal

    }

}
