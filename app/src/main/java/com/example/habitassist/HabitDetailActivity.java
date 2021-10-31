package com.example.habitassist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HabitDetailActivity extends AppCompatActivity {
    private TextView habitDetailTitle, habitDetailStartDate, habitDetailReason, habitDetailDaysToDo;
    private Button habitDetailDeleteButton, habitDetailEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassed");

        habitDetailTitle = (TextView) findViewById(R.id.habit_detail_title);
        habitDetailStartDate = (TextView) findViewById(R.id.habit_detail_date);
        habitDetailReason = (TextView) findViewById(R.id.habit_detail_reason);
        habitDetailDaysToDo = (TextView) findViewById(R.id.habit_detail_days_to_do);
        habitDetailDeleteButton = (Button) findViewById(R.id.habit_detail_delete_button);
        habitDetailEditButton = (Button) findViewById(R.id.habit_detail_edit_button);

        habitDetailTitle.setText(habit.getHabitTitle());
        habitDetailStartDate.setText("Date started: " + habit.getStartDate());
        habitDetailReason.setText(habit.getReason());
        habitDetailDaysToDo.setText(habit.getDaysToBeDone());

        // TODO: Determine here if this habit actually belongs to the correct profile in database
        if (true) {
            habitDetailDeleteButton.setVisibility(View.VISIBLE);
            habitDetailEditButton.setVisibility(View.VISIBLE);
        }

    }
}