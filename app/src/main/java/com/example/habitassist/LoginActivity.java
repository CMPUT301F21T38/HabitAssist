package com.example.habitassist;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
    }

    public void LoginButton(View view){
        EditText username =  (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.Password);

        if (!username.getText().toString().equals("")) {
            DocumentReference docRef = db.collection("profiles").document(username.getText().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            if (document.get("password").equals(password.getText().toString())) {
                                //send user profile back to main activity
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", username.getText().toString());
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "No such document");

                            Toast.makeText(getApplicationContext(), "Username does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateAccount(View view){
        EditText username =  (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.Password);

        DocumentReference docRef = db.collection("profiles").document(username.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Toast.makeText(getApplicationContext(), "Username already taken, please try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "No such document");

                        Profile newProfile = new Profile(username.getText().toString(), password.getText().toString());
                        String name = newProfile.getUsername();
                        HashMap<String, String> profileDocument = newProfile.getDocument();
                        db.collection("profiles").document(name).set(profileDocument);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", username.getText().toString());
                        setResult(Activity.RESULT_OK,returnIntent);

                        finish();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
