package com.example.habitassist;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class AddHabitActivity extends AppCompatActivity {
    ArrayList<String> MedTempo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        Intent intent = getIntent();
    }

    public void SaveButton(View view){
        MedTempo2 = new ArrayList<>();
        EditText title_added = (EditText) findViewById(R.id.editTextTextPersonName);

        Button saveButton = (Button) findViewById(R.id.button);

        EditText reason_added = (EditText) findViewById(R.id.editTextTextPersonName2);

        DatePicker take_date = (DatePicker) findViewById(R.id.editTextDate2);
        int take_day = take_date.getDayOfMonth();
        String take_day1 = Integer.toString(take_day);
        if (take_day < 10){
            take_day1 = "0" + take_day1;
        }
        int take_month = take_date.getMonth() + 1;
        int take_year = take_date.getYear();

        String take_month1 = Integer.toString(take_month);
        if (take_month < 10){
            take_month1 = "0" + take_month1;
        }
        String take_year1 = Integer.toString(take_year);
        String date_Started = take_year1 + "-" + take_month1 + "-" + take_day1;

        if (((CheckBox) findViewById(R.id.checkbox_Monday)).isChecked()) {
            MedTempo2.add("Monday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Tuesday)).isChecked()) {
            MedTempo2.add("Tuesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Wednesday)).isChecked()) {
            MedTempo2.add("Wednesday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Thursday)).isChecked()) {
            MedTempo2.add("Thursday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Friday)).isChecked()) {
            MedTempo2.add("Friday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Saturday)).isChecked()) {
            MedTempo2.add("Saturday");
        }
        if (((CheckBox) findViewById(R.id.checkbox_Sunday)).isChecked()) {
            MedTempo2.add("Sunday");
        }

        if (title_added.getText().toString().length() > 20) {
            Toast.makeText(getApplicationContext(),"Please keep the title under 20 characters",Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() > 30) {
            Toast.makeText(getApplicationContext(), "Please keep the reason under 30 characters", Toast.LENGTH_SHORT).show();
        }
        if (reason_added.getText().toString().length() <= 30 && title_added.getText().toString().length() <= 20) {
            Habit habit1 = new Habit(title_added.getText().toString(),reason_added.getText().toString(),date_Started, TextUtils.join(", ", MedTempo2));
            Intent intent_add = new Intent(view.getContext(), MainActivity.class);
            intent_add.putExtra("Object", (Serializable) habit1);
            setResult(Activity.RESULT_OK, intent_add);
            finish();
        }
        /*
        Habit habit1 = new Habit(main_title,main_reason,date_Started, TextUtils.join(", ", MedTempo2));
        Intent intent_add = new Intent(view.getContext(), MainActivity.class);
        intent_add.putExtra("Object", (Serializable) habit1);
        setResult(Activity.RESULT_OK, intent_add);
        finish();
         */
    }

    public void CancelButton(View view){
        // do something when the cancel button is pressed
        finish();
    }
}