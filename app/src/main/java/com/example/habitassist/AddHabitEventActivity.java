package com.example.habitassist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddHabitEventActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Habit habitRecieved;

    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit_event);

        db = FirebaseFirestore.getInstance();
        habitRecieved = (Habit) getIntent().getSerializableExtra("habit");

        commentEditText = (EditText) findViewById(R.id.comment_edit_text);
    }

    public void onClickSaveButton(View view) {
        String comment = commentEditText.getText().toString();
        if (comment.length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the comment under 20 characters", Toast.LENGTH_SHORT).show();
        } else {
            // Added it to firebase
            // Source: https://stackoverflow.com/a/23068721/8270982
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            String title = habitRecieved.getHabitTitle();

            HabitEvent habitEvent = new HabitEvent(title, timeStamp, comment);
            String uniqueHabitEventID = title + "*" + timeStamp;
            HashMap<String, String> habitEventDocument = habitEvent.getDocument();
            db.collection("habitEvents").document(uniqueHabitEventID).set(habitEventDocument);
            finish();
        }
    }


    public void onClickCancelButton(View view) {
        finish();
    }
}