package com.example.habitassist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HabitEventEditActivity extends AppCompatActivity {


    FirebaseFirestore db;
    HabitEvent habitEventRecieved;

    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_event_edit);

        db = FirebaseFirestore.getInstance();
        habitEventRecieved = (HabitEvent) getIntent().getSerializableExtra("habitEventPassed");

        commentEditText = (EditText) findViewById(R.id.comment_edit_text);

        commentEditText.setText(habitEventRecieved.getComment());
    }

    public void onClickSaveButton(View view) {
        String comment = commentEditText.getText().toString();
        if (comment.length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the comment under 20 characters", Toast.LENGTH_SHORT).show();
        } else {
            String title = habitEventRecieved.getHabitTitle();
            String timeStamp = habitEventRecieved.getTimeStamp();
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