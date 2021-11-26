package com.example.habitassist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        MainActivity mainActivityInstance = MainActivity.getInstance();
        String username = mainActivityInstance.getUsername();

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("profiles").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        String username = (String) data.get("username");
                        String password = (String) data.get("password");
                        String followRequests = (String) data.get("followRequests");
                        String following = (String) data.get("following");
                        //convert the follow requests and following to their respective lists.
                        // Add a guard in case a wrongly structured Habit data is put into firestore
                        if (username != null && password != null && followRequests != null && following != null) {
                            String[] requests = followRequests.split(",");
                            String[] followed = following.split(",");

                            Profile profile = new Profile(username, password);

                            for (String names : requests) {
                                profile.AddFollowRequests(names);
                            }
                            for (String names : followed) {
                                profile.addFollowing(names);
                            }
                        }
                    }
                }
            }
        });
        //Add code to display following and follow requests




    }

    public void ProfileButton(View view){
        MainActivity mainActivityInstance = MainActivity.getInstance();
        mainActivityInstance.ProfileButton(view);
        finish();

    }

    public void HomeButton(View view){
        finish();

    }
}
