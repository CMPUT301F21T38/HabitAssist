package com.example.habitassist;
import android.app.Activity;
import android.app.Instrumentation;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotium.solo.Solo;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class HabitDetailTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * A utilty class to add a Habit
     */
    // public static void addHabit(Solo solo) {
    //     LocalDate today = LocalDate.now();
    //     LocalDate tomorrow = today.plusDays(1);
    //     String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(today);
    //     String nextDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(tomorrow);
    //     Habit myHabit1 = new Habit("testt_gym", "stay fit", "2021-01-01", currentDayOfTheWeek);
    //
    //     // Click on the add_habit_button
    //     // Check that it navigates to the right activity
    //     // Try entering a new Habit
    //     Habit myHabit2 = new Habit("testt_cook", "yum", "1770-11-04", nextDayOfTheWeek);
    //
    //     solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), myHabit.getHabitTitle());
    //     //
    //     //
    //     //
    //
    //     // Click on Save
    //     // Assert that it navigates to the right activity
    //
    //     // Add another habit on a different day
    //     Habit myHabit2 = new Habit("testt_cook", "yum", "1770-11-04", "Wednesday, Friday, Saturday");
    //     solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), myHabit.getHabitTitle());
    //     //
    //     //
    //     //
    //
    //     // Click on Save
    //     // Assert that it navigates to the right activity
    //
    //
    // }

    @Before
    public void setUp() throws Exception {
        // Starting at the MainActivity is a necessary step for the app architecture to work
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    @Test
    public void Should_Be_Able_To_Add_Habit() {
        //
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
