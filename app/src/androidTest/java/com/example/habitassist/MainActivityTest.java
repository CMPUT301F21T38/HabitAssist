/*
 * This file implements User-Interface Tests for various intents and functionalities
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotium.solo.Solo;
import androidx.test.rule.ActivityTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MainActivityTest {
    private Solo solo;
    private Habit myExampleHabitToday, myExampleHabitTomorrow;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Create two Habits, one for today and the other for a different day. This method runs once
     * before each test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        MainActivity mainActivityInstance = MainActivity.getInstance();

        // Create two habits for the tests
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(todayDate);
        String nextDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(tomorrowDate);
        String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(todayDate);
        myExampleHabitToday = new Habit("testt_gym", "stay fit", dateString, currentDayOfTheWeek, mainActivityInstance.getUsername());
        myExampleHabitTomorrow = new Habit("testt_cook", "yum", dateString, nextDayOfTheWeek, mainActivityInstance.getUsername());
    }

    @Test
    public void testTheAddingEditingAndDeletingOfHabits() {
        // Click on the plus button
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.add_habit_button));

        // Check that we are on the AddHabitActivity page
        solo.assertCurrentActivity(
                "Click on the plus button does not navigate to AddHabitActivity",
                AddHabitActivity.class);

        // Test title
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), "1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), myExampleHabitToday.getHabitTitle());

        // Test reason
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), "1234567890 1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName2));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), myExampleHabitToday.getReason());

        //
        solo.clickOnText(myExampleHabitToday.getDaysToBeDone());

        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save

        solo.assertCurrentActivity("", MainActivity.class);

        // Add new habit
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.add_habit_button));
        solo.assertCurrentActivity(
                "Click on the plus button does not navigate to AddHabitActivity",
                AddHabitActivity.class);

        // Test title
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), "1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName), myExampleHabitTomorrow.getHabitTitle());

        // Test reason
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), "1234567890 1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName2));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), myExampleHabitTomorrow.getReason());

        //
        solo.clickOnText(myExampleHabitTomorrow.getDaysToBeDone());

        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save

        solo.assertCurrentActivity("", MainActivity.class);


        // The habit scheduled for today should show up in the home page
        assertTrue(solo.searchText(myExampleHabitToday.getHabitTitle()));

        // The habit scheduled for tomorrow should not show up in the home page
        assertTrue(!solo.searchText(myExampleHabitTomorrow.getHabitTitle()));

        solo.clickOnText(myExampleHabitToday.getHabitTitle());

        // The correct activity should open up
        solo.assertCurrentActivity(
                "Clicking on a habit title on the home page does not open HabitDetail page",
                HabitDetailActivity.class);
        // The details should match the habit we clicked on
        assertEquals(((TextView) solo.getView(R.id.habit_detail_title)).getText(), myExampleHabitToday.getHabitTitle());
        assertEquals(((TextView) solo.getView(R.id.habit_detail_reason)).getText(), myExampleHabitToday.getReason());
        assertTrue(((String)((TextView) solo.getView(R.id.habit_detail_date)).getText()).contains(myExampleHabitToday.getStartDate()));
        assertEquals(((TextView) solo.getView(R.id.habit_detail_days_to_do)).getText(), myExampleHabitToday.getDaysToBeDone());

        // TODO: Once profiles are implemented; these should only appear if the habit belongs to the profile
        if (true) {
            assertTrue(solo.searchButton("Edit"));
            assertTrue(solo.searchButton("Delete"));
        }
        solo.goBack();
        solo.assertCurrentActivity(
                "Does not return to MainActivity from the Habitdetail page when cancel is clicked",
                MainActivity.class);

        solo.clickOnView((Button) solo.getView(R.id.profile_button)); // Click on Profile button

        solo.assertCurrentActivity("", ProfileActivity.class);

        // All habits should show up in the profile page
        assertTrue(solo.searchText(myExampleHabitToday.getHabitTitle()));
        assertTrue(solo.searchText(myExampleHabitTomorrow.getHabitTitle()));

        solo.clickOnText(myExampleHabitTomorrow.getHabitTitle());

        // The correct activity should open up
        solo.assertCurrentActivity(
                "Clicking on a habit title on the profile page does not open HabitDetail page",
                HabitDetailActivity.class);
        // The details should match the habit we clicked on
        assertEquals(((TextView) solo.getView(R.id.habit_detail_title)).getText(), myExampleHabitTomorrow.getHabitTitle());
        assertEquals(((TextView) solo.getView(R.id.habit_detail_reason)).getText(), myExampleHabitTomorrow.getReason());
        assertTrue(((String)((TextView) solo.getView(R.id.habit_detail_date)).getText()).contains(myExampleHabitTomorrow.getStartDate()));
        assertEquals(((TextView) solo.getView(R.id.habit_detail_days_to_do)).getText(), myExampleHabitTomorrow.getDaysToBeDone());

        // TODO: Once profiles are implemented; these should only appear if the habit belongs to the profile
        if (true) {
            assertTrue(solo.searchButton("Edit"));
            assertTrue(solo.searchButton("Delete"));
        }

        // Delete the two habits we added
        solo.clickOnView((Button) solo.getView(R.id.habit_detail_delete_button));
        solo.assertCurrentActivity("", ProfileActivity.class);
        solo.goBack();
        solo.clickOnText(myExampleHabitToday.getHabitTitle());
        solo.assertCurrentActivity("", HabitDetailActivity.class);
        solo.clickOnView((Button) solo.getView(R.id.habit_detail_delete_button));
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
