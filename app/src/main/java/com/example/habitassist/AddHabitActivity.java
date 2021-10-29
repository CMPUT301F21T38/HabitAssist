package com.example.habitassist;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

        Habit habit1 = new Habit(title,reason,date_Started,MedTempo2);
        Intent intent_add = new Intent(view.getContext(), MainActivity.class);
        intent_add.putExtra("Object", (Serializable) habit1);
        setResult(Activity.RESULT_OK, intent_add);
        finish();



    }

    public void CancelButton(View view){
        // do something when the cancel button is pressed
        finish();
    }
}