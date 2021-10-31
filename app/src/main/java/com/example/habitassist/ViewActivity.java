package com.example.habitassist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private TextView title_text,reason_text, date_text, days_text;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent i = getIntent();
        Habit habit_view = (Habit) i.getSerializableExtra("habitPassed");
        title_text = (TextView) findViewById(R.id.textView10);
        reason_text = (TextView) findViewById(R.id.textView11);
        date_text = (TextView) findViewById(R.id.textView13);
        days_text = (TextView) findViewById(R.id.textView9);

        String tempo_title = habit_view.getHabitTitle();
        String tempo_reason = habit_view.getReason();
        String tempo_date = habit_view.getStartDate();
        title_text.setText(tempo_title);
        reason_text.setText(tempo_reason);
        date_text.setText(tempo_date);

        String days_to_be_done;
        days_to_be_done = String.join(", ", habit_view.getDaysToBeDone());
        days_text.setText(days_to_be_done);

    }
}