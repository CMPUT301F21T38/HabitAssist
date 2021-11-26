package com.example.habitassist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Profile profile;

    private ArrayAdapter<String> followRequestAdapter;
    private ArrayAdapter<String> followingAdapter;

    private ListView followRequestListView;
    private ListView followingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        MainActivity mainActivityInstance = MainActivity.getInstance();
        String username = mainActivityInstance.getUsername();


        db = FirebaseFirestore.getInstance();
        /*DocumentReference docRef = db.collection("profiles").document(username);
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

                           profile = new Profile(username, password);

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
        });*/
        //Add code to display following and follow requests
        followingListView = (ListView) findViewById(R.id.following);
        followRequestListView = (ListView) findViewById(R.id.followRequests);

        //do some stuff
        profile = new Profile();

        db.collection("profiles").document(mainActivityInstance.getUsername()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException error) {

                if (error == null && doc != null && doc.exists()) {
                    Map<String, Object> data = doc.getData();
                    String username = (String) data.get("username");
                    String password = (String) data.get("password");
                    String followRequests = (String) data.get("followRequests");
                    String following = (String) data.get("following");
                    //convert the follow requests and following to their respective lists.
                    // Add a guard in case a wrongly structured Habit data is put into firestore
                    if (username != null && password != null && followRequests != null && following != null) {
                        String[] requests = followRequests.split(", ");
                        String[] followed = following.split(", ");
                        profile.setUsername(username);
                        profile.setPassword(password);

                        profile.clearFollowers();
                        profile.clearFollowRequests();

                        for (String names : requests) {
                            profile.AddFollowRequests(names);
                        }
                        for (String names : followed) {
                            profile.addFollowing(names);
                        }
                    }

                }

                followRequestAdapter.notifyDataSetChanged();
                followingAdapter.notifyDataSetChanged();
            }
        });

        followRequestAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, profile.getFollowRequests());
        followRequestListView.setAdapter(followRequestAdapter);

        followingAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, profile.getFollowing());
        followingListView.setAdapter(followingAdapter);




    }

    public void ProfileButton(View view){
        MainActivity mainActivityInstance = MainActivity.getInstance();
        mainActivityInstance.ProfileButton(view);
        finish();

    }

    public void HomeButton(View view){
        finish();

    }

    public void SearchBar(View view){
        //search for other users
        EditText search = (EditText) findViewById(R.id.SearchName);
        String SearchUser = search.getText().toString();
        //for functionality purposes I will only search the database for the username and later if we want
        //we can add the search for items like this username and display them in a new activity
        MainActivity mainActivityInstance = MainActivity.getInstance();
        db.collection("profiles").document(SearchUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //add the username to profile list
                                Map<String, Object> data = document.getData();
                                String requests = (String) data.get("followRequests");
                                if (requests.isEmpty()){
                                    requests = mainActivityInstance.getUsername();
                                }else{
                                    //from the comma separated list and check if username already exists
                                    requests = requests + ", " + mainActivityInstance.getUsername();
                                }
                                db.collection("profiles").document(SearchUser).update("followRequests", requests);
                                System.out.println("--------------");
                                System.out.println(requests);
                                System.out.println("--------------");
                            } else {
                                //toast message "user does not exist"

                            }
                        }
                    }
                });


        /*Intent intent = new Intent(this, FeedActivity.class);
        intent.putExtra(SearchUser);
        startActivity(intent);*/

    }
}
