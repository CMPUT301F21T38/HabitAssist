package com.example.habitassist;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.util.Log;
>>>>>>> javin
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habitassist.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
    private FloatingActionButton myFab;
    public ArrayList<Habit> habitList;
    ArrayAdapter<String> habitAdapter;
    ArrayList<String> habitList2;
    private ListView listview;

    public FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        habitList = new ArrayList<>();
        habitList2 = new ArrayList<>();
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
        CollectionReference habitsCollection = db.collection("habits");
        habitsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                habitList.clear();
                habitList2.clear();
                for (QueryDocumentSnapshot doc: value) {
                    Map<String, Object> data = doc.getData();
                    String title = (String) data.get("title");
                    String reason = (String) data.get("reason");
                    String startDate = (String) data.get("startDate");
                    String daysToBeDone = (String) data.get("daysToBeDone");
                    if (title != null && reason != null && startDate != null && daysToBeDone != null) {
                        Habit habit = new Habit(title, reason, startDate, daysToBeDone);
                        habitList.add(habit);
                        if (habit.isForToday()) {
                            habitList2.add(title);
                        }
                    }
                }
                habitAdapter.notifyDataSetChanged();
            }
        });
        habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, habitList2);
        listview.setAdapter(habitAdapter);

//        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        myFab = (FloatingActionButton) findViewById(R.id.add_habit_button);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddMainHabit();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // it returns the index of the name of the medicine from the Listview OR the ArrayList
                Intent intent3 = new Intent(MainActivity.this, HabitDetailActivity.class);
                intent3.putExtra("habitPassed", habitList.get(i));
                startActivityForResult(intent3, 5);

            }
        });
    }

    public void AddMainHabit(){
        System.out.println("AddMainHabit entered1");
        Intent intent1 = new Intent(this, AddHabitActivity.class);
        startActivityForResult(intent1, 3);
    }

    public void addHabit(View view){
        //Getting information from each text location and adding creating a new habit with that information
        //start the new activity where we create a new habit and get the information.



    }

    public void cancelHabit(View view){
        //if cancel button is clicked then remove information from text boxes and return to previous screen

    }

    public void ProfileButton(View view){
        Intent intent2 = new Intent(this, ProfileActivity.class);
        intent2.putExtra("habit2", habitList);
        startActivityForResult(intent2, 4);
    }

    public void FeedButton(View view){

    }
    public void HomeButton(View view){
        // do something when the home button is clicked
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            Habit habit_main = (Habit) data.getSerializableExtra("Object");
            habitList.add(habit_main);
            String title = habit_main.getHabitTitle();
            HashMap<String, String> habitDocument = habit_main.getDocument();
            db.collection("habits").document(title).set(habitDocument);
        }
    }
}