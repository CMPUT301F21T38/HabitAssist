package com.example.habitassist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Arrays;
import java.util.List;
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


        db = FirebaseFirestore.getInstance();
        //Add code to display following and follow requests
        followingListView = (ListView) findViewById(R.id.following);
        followRequestListView = (ListView) findViewById(R.id.followRequests);

        //do some stuff
        profile = new Profile();

        db.collection("profiles").document(SingletonUsername.get()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        String[] requests = {followRequests};
                        String[] followed = {following};
                        if (followRequests.indexOf(",") != -1) {
                            requests = followRequests.split(",");
                        }


                        if (following.indexOf(",") != -1) {
                            followed = following.split(",");
                        }

                        profile.setUsername(username);
                        profile.setPassword(password);

                        profile.clearFollowers();
                        profile.clearFollowRequests();

                        for (String names : requests) {
                            if (!names.isEmpty()) {
                                profile.AddFollowRequests(names);
                            }
                        }
                        for (String names : followed) {
                            if (!names.isEmpty()) {
                                profile.addFollowing(names);
                            }
                        }
                    }

                }

                followRequestAdapter.notifyDataSetChanged();
                followingAdapter.notifyDataSetChanged();

                //Dynamically update height
                LinearLayout followRequestsLayout = (LinearLayout) findViewById(R.id.followrequests_linearlayout);
                LinearLayout followingLayout = (LinearLayout) findViewById(R.id.following_linearlayout);

                ViewGroup.LayoutParams followRequestsLayoutParams = followRequestsLayout.getLayoutParams();
                float dpFactor = getApplicationContext().getResources().getDisplayMetrics().density;
                int dpPerItem = 51;
                int offsetInDp = 80;
                int nFollowRequests = profile.getFollowRequests().size();
                int height = (int) ((nFollowRequests * dpPerItem + offsetInDp) * dpFactor);
                followRequestsLayoutParams.height = nFollowRequests == 0 ? 0 : height;
                followRequestsLayout.setLayoutParams(followRequestsLayoutParams);
                followRequestsLayout.requestLayout();
                int nFollowing = profile.getFollowing().size();
                height = (int) ((nFollowing * dpPerItem + offsetInDp) * dpFactor);
                ViewGroup.LayoutParams followingLayoutParams = followingLayout.getLayoutParams();
                followingLayoutParams.height = nFollowing == 0 ? 0 : height;
                followingListView.setLayoutParams(followingLayoutParams);
                followingListView.requestLayout();
            }
        });

        followRequestAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, profile.getFollowRequests());
        followRequestListView.setAdapter(followRequestAdapter);

        followingAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, profile.getFollowing());
        followingListView.setAdapter(followingAdapter);

        followingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (profile.getFollowing().isEmpty()) {
                    return;
                }
                // it returns the name of the habit from the Listview OR the ArrayList
                String user = profile.getFollowing().get(i);
                Intent intent = new Intent(FeedActivity.this, ProfileActivity.class);
                intent.putExtra("usernameOfFollowing", user);
                startActivity(intent);

            }
        });

        // Accept/delete follow requests
        db.collection("profiles").document(SingletonUsername.get())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException error) {
                        if (error == null && document != null && document.exists()) {
                            followRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    //Accept button clicked
                                                    String requester = profile.getFollowRequests().get(i);

                                                    // add the follower to the requester's profile
                                                    DocumentReference docRef = db.collection("profiles").document(profile.getFollowRequests().get(i));
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                            if (task2.isSuccessful()) {
                                                                DocumentSnapshot document2 = task2.getResult();
                                                                if (document2.exists()) {
                                                                    String following = (String) document2.getData().get("following");
                                                                    // remake the string, accounting for edge cases
                                                                    if (following.equals("")) {
                                                                        following = profile.getUsername();
                                                                    } else {
                                                                        following = following + "," + profile.getUsername();
                                                                    }
                                                                    // update db
                                                                    db.collection("profiles").document(requester).update("following", following);
                                                                }
                                                            }
                                                        }
                                                    });

                                                    // delete the request from the users requests
                                                    ArrayList<String> requests_list = new ArrayList<>(profile.getFollowRequests());
                                                    requests_list.remove(profile.getFollowRequests().get(i));
                                                    String requests = TextUtils.join(",", requests_list);
                                                    // update database
                                                    db.collection("profiles").document(profile.getUsername()).update("followRequests", requests);

                                                    Toast.makeText(FeedActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // update database
                                                    requests_list = new ArrayList<String>(profile.getFollowRequests());
                                                    requests_list.remove(profile.getFollowRequests().get(i));
                                                    requests = TextUtils.join(",", requests_list);
                                                    db.collection("profiles").document(profile.getUsername()).update("followRequests", requests);

                                                    Toast.makeText(FeedActivity.this, "Request Deleted " + requests, Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setMessage("Follow Request")
                                            .setPositiveButton("Accept", dialogClickListener)
                                            .setNegativeButton("Delete", dialogClickListener).show();


                                }
                            });
                        } else {
                            Toast.makeText(FeedActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void ProfileButton(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }

    public void HomeButton(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        finish();
    }

    public void SearchBar(View view){
        //search for other users
        EditText search = (EditText) findViewById(R.id.SearchName);
        String SearchUser = search.getText().toString();
        //for functionality purposes I will only search the database for the username and later if we want
        //we can add the search for items like this username and display them in a new activity
        MainActivity mainActivityInstance = MainActivity.getInstance();
        String myUsername = SingletonUsername.get(); mainActivityInstance.getUsername();

        if (SearchUser.equals(myUsername)) {
            Toast.makeText(this, "You cannot follow yourself 😄", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("profiles").document(SearchUser)
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //add the username to profile list
                            Map<String, Object> data = document.getData();
                            String requests = (String) data.get("followRequests");
                            if (requests.isEmpty()){
                                requests = myUsername;
                            }else{
                                //from the comma separated list and check if username already exists
                                if (!requests.contains(myUsername))
                                {
                                    requests = requests + "," + myUsername;
                                }else{
                                    Toast.makeText(FeedActivity.this, "Request Already Sent", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }
                            db.collection("profiles").document(SearchUser).update("followRequests", requests);
                            Toast.makeText(FeedActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            //toast message "user does not exist"
                            Toast.makeText(FeedActivity.this, "User Does Not Exist", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


        /*Intent intent = new Intent(this, FeedActivity.class);
        intent.putExtra(SearchUser);
        startActivity(intent);*/

    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Exiting HabitAssist")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
