package com.example.habitassist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
    public FloatingActionButton myFab;
    ArrayList<Habit> habitList;
    ArrayAdapter<String> habitAdapter;
    ArrayList<String> habitList2;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);

        habitList = new ArrayList<>();

        habitList2 = new ArrayList<>();
        habitAdapter = new ArrayAdapter<>(this, R.layout.profile_content, R.id.list_item, habitList2);
        //habitAdapter = new CustomList(this, habitList2);

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
    }

    public void AddMainHabit(){
        System.out.println("AddMainHabit entered1");
        Intent intent1 = new Intent(this, AddHabitActivity.class);
        startActivityForResult(intent1, 3);
    }

    public void addHabit(){
        //Getting information from each text location and adding creating a new habit with that information
        //After creating habit we reset the text boxes and date to original settings

    }

    public void cancelHabit(){
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
            String date_tempo = habit_main.getStartDate();
            String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
            if (date_tempo.equals(dateString)){
                String title_tempo = habit_main.getHabitTitle();
                habitList2.add(title_tempo);
            }
            String temp = habitList2.toString();
            //((TextView) findViewById(R.id.textView5)).setText(temp);
            habitAdapter.notifyDataSetChanged();
        }
    }
}