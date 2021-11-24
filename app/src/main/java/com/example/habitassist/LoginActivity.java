package com.example.habitassist;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
    }

    public void LoginButton(View view){

        finish();
    }

    public void CreateAccount(View view){
        EditText username =  (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.Password);

        Profile newProfile = new Profile(username.getText().toString(), password.getText().toString());
        String name = newProfile.getUsername();
        HashMap<String, String> profileDocument = newProfile.getDocument();
        db.collection("profiles").document(name).set(profileDocument);
        finish();
    }
}
