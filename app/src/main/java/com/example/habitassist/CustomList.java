/*
 * This file implements the class CustomList
 * -------------------------------------------------------------------------------------------------
 *
 * Copyright [2021] [CMPUT301F21T38]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.habitassist;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * An ArrayAdapter used to display data into a ListView
 *
 * Currently outstanding issues: This class is UNFINISHED and UNUSED right now.
 * It will be used for HabitEvents later.
 */
public class CustomList extends ArrayAdapter<Habit> {
    //Declaring variables as understood in CustomList Lab 3
    private ArrayList<Habit> habits;
    private Context context;



    //Constructor (invoked when CustomList called through mainActivity)
    public CustomList(Context context, ArrayList<Habit> habits){
        super(context,0, habits);
        this.habits = habits;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.content,parent,false);

        }
        Habit habit = habits.get(position);

        //Linking TextViews present in our content.xml to UI
        TextView title = view.findViewById(R.id.Title_text);
        TextView reason = view.findViewById(R.id.Reason_text);
        TextView date = view.findViewById(R.id.Date_text);
        TextView days = view.findViewById(R.id.Days_In_week);

        if(habit!=null) {
            //setting the values of respective variables through their respective getters

            title.setText(habit.getHabitTitle());
            //setting the value of an int as a string
            //Reference: Guillaume, January 8, 2012, CC BY-SA 3.0, https://stackoverflow.com/questions/8781535/set-text-to-integer-value

            reason.setText(habit.getReason());


            date.setText((CharSequence) habit.getStartDate());

            //setting the value of an int as a string
            //Reference: Guillaume, January 8, 2012, CC BY-SA 3.0, https://stackoverflow.com/questions/8781535/set-text-to-integer-value
            String days_to_be_done;
            days_to_be_done = String.join(", ", habit.getDaysToBeDone());
            days.setText(days_to_be_done);

        }
        return view;
    }
}
