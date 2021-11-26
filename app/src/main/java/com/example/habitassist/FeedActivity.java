package com.example.habitassist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FeedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        MainActivity mainActivityInstance = MainActivity.getInstance();
        String username = mainActivityInstance.getUsername();


        //set up both the list views. following and follow requests

}
}
