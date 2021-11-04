package com.example.habitassist;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class HabitEditActivity extends AppCompatActivity {
    ArrayList<String> HabitDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("EditHabitActivity entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);
        Intent intent = getIntent();

        // get the habit to edit passed in from MainActivity
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassedIn");

        // get the edittext and datepicker objects
        EditText title = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText reason = (EditText) findViewById(R.id.editTextTextPersonName2);
        //DatePicker date = (DatePicker) findViewById(R.id.editTextDate2);

        // set the habit values that have already been given previously
        title.setText(habit.getHabitTitle());
        reason.setText(habit.getReason());

        // set the calandar on the original start date
        //String[] original_date = habit.getStartDate().split("-");
        //date.init(Integer.parseInt(original_date[0]), Integer.parseInt(original_date[1]), Integer.parseInt(original_date[2]), null);

        // check day boxes that have already been checked
        String[] days = habit.getDaysToBeDone().split(",");

        if (Arrays.asList(days).contains("Monday")) {
            CheckBox monday = findViewById(R.id.checkbox_Monday);
            monday.setChecked(true);
        }

        if (Arrays.asList(days).contains(" Tuesday")) {
            CheckBox tuesday = findViewById(R.id.checkbox_Tuesday);
            tuesday.setChecked(true);
        }

        if (Arrays.asList(days).contains(" Wednesday")) {
            CheckBox wed = findViewById(R.id.checkbox_Wednesday);
            wed.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Thursday")) {
            CheckBox thurs = findViewById(R.id.checkbox_Thursday);
            thurs.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Friday")) {
            CheckBox fri = findViewById(R.id.checkbox_Friday);
            fri.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Saturday")) {
            CheckBox sat = findViewById(R.id.checkbox_Saturday);
            sat.setChecked(true);
        }
        if (Arrays.asList(days).contains(" Sunday")) {
            CheckBox sun = findViewById(R.id.checkbox_Sunday);
            sun.setChecked(true);
        }

    }

    public void SaveButton(View view){
        HabitDays = new ArrayList<>();

        // get the habit to edit passed in from MainActivity
        Habit habit = (Habit) getIntent().getSerializableExtra("habitPassedIn");

        // get the edittext and datepicker objects
        EditText title_added = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText reason_added = (EditText) findViewById(R.id.editTextTextPersonName2);
        //DatePicker take_date = (DatePicker) findViewById(R.id.editTextDate2);

        // get the title and reason from the user
        String main_title = title_added.getText().toString();
        String main_reason = reason_added.getText().toString();

        // get the date and convert it
        //int take_day = take_date.getDayOfMonth();
        //String take_day1 = Integer.toString(take_day);
        //if (take_day < 10){
        /*    take_day1 = "0" + take_day1;
        }
        int take_month = take_date.getMonth() + 1;
        int take_year = take_date.getYear();

        String take_month1 = Integer.toString(take_month);
        if (take_month < 10){
            take_month1 = "0" + take_month1;
        }
        String take_year1 = Integer.toString(take_year);
        String date_Started = take_year1 + "-" + take_month1 + "-" + take_day1;*/

        if (((CheckBox) findViewById(R.id.checkbox_Monday)).isChecked()) {
            HabitDays.add("Monday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Tuesday)).isChecked()) {
            HabitDays.add("Tuesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Wednesday)).isChecked()) {
            HabitDays.add("Wednesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Thursday)).isChecked()) {
            HabitDays.add("Thursday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Friday)).isChecked()) {
            HabitDays.add("Friday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Saturday)).isChecked()) {
            HabitDays.add("Saturday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Sunday)).isChecked()) {
            HabitDays.add("Sunday");
        }

        // edit the habit object
        habit.setTitle(main_title);
        habit.setReason(main_reason);
        //habit.setStartDate(date_Started);
        habit.setDaysToBeDone(TextUtils.join(", ", HabitDays));
        Button saveButton = (Button) findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title_added.getText().toString().length() > 20) {
                    Toast.makeText(getApplicationContext(), "Please keep the title under 20 characters", Toast.LENGTH_SHORT).show();
                }
                if (reason_added.getText().toString().length() > 30) {
                    Toast.makeText(getApplicationContext(), "Please keep the reason under 30 characters", Toast.LENGTH_SHORT).show();
                }
                if (reason_added.getText().toString().length() <= 30 && title_added.getText().toString().length() <= 20) {
                    Intent intent_add = new Intent(view.getContext(), MainActivity.class);
                    intent_add.putExtra("Object", (Serializable) habit);
                    setResult(Activity.RESULT_OK, intent_add);
                    finish();
                }
            }
        });


    }

    public void CancelButton(View view){
        // do something when the cancel button is pressed
        finish();
    }
}
