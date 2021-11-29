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
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;
import androidx.test.rule.ActivityTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

        // Create two habits for the tests
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(todayDate);
        String nextDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(tomorrowDate);
        String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(todayDate);
        String reason = "fun", ownerUsername = "waseem";
        boolean isPublic = true;
        myExampleHabitToday = new Habit("solorobothabit1", reason, dateString, currentDayOfTheWeek, ownerUsername, isPublic, "2021-09-01 12:12:12");
        myExampleHabitTomorrow = new Habit("solorobothabit2", reason, dateString, nextDayOfTheWeek, ownerUsername, isPublic, "2021-09-01 10:10:10");
    }

    @Test
    public void test1_CreateNewAccount() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");
        solo.clickOnButton("Create Account");
        solo.clickOnButton("Yes");
        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the create account button does not navigate to HomeActivity",
                HomeActivity.class);
    }

    @Test
    public void test2_LoginToAccount() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);
    }


    @Test
    public void test3_CreateHabitForToday() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        // Click on the plus button
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.add_habit_button));

        // Check that we are on the AddHabitActivity page
        solo.assertCurrentActivity(
                "Click on the plus button does not navigate to AddHabitActivity",
                AddHabitActivity.class);

        // Test title character limit
        solo.enterText((EditText) solo.getView(R.id.comment_edit_text), "1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("The title is not limited to 20 characters", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.comment_edit_text));
        solo.enterText((EditText) solo.getView(R.id.comment_edit_text), myExampleHabitToday.getHabitTitle());

        // Test reason character limit
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), "1234567890 1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("The reason is not limited to 30 characters", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName2));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), myExampleHabitToday.getReason());

        // Choose the current day of the week to do
        solo.clickOnText(myExampleHabitToday.getDaysToBeDone());

        // click on save
        solo.clickOnView((Button) solo.getView(R.id.button));

        solo.assertCurrentActivity("Saving a habit did not work", HomeActivity.class);
    }

    @Test
    public void test4_CreateHabitForTomorrow() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        // Click on the plus button
        // Add new habit
        solo.clickOnView((FloatingActionButton) solo.getView(R.id.add_habit_button));
        solo.assertCurrentActivity(
                "Click on the plus button does not navigate to AddHabitActivity",
                AddHabitActivity.class);

        // Test title
        solo.enterText((EditText) solo.getView(R.id.comment_edit_text), "1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.comment_edit_text));
        solo.enterText((EditText) solo.getView(R.id.comment_edit_text), myExampleHabitTomorrow.getHabitTitle());

        // Test reason
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), "1234567890 1234567890 1234567890 1");
        solo.clickOnView((Button) solo.getView(R.id.button)); // click on save
        solo.assertCurrentActivity("", AddHabitActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.editTextTextPersonName2));
        solo.enterText((EditText) solo.getView(R.id.editTextTextPersonName2), myExampleHabitTomorrow.getReason());

        // Choose the next
        solo.clickOnText(myExampleHabitTomorrow.getDaysToBeDone());

        // click on save
        solo.clickOnView((Button) solo.getView(R.id.button));

        solo.assertCurrentActivity("Saving a habit did not work", HomeActivity.class);
    }



    @Test
    public void test5_ScheduleForToday() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        // The habit scheduled for today should show up in the home page
        assertTrue(solo.searchText(myExampleHabitToday.getHabitTitle()));

        // The habit scheduled for tomorrow should not show up in the home page
        assertTrue(!solo.searchText(myExampleHabitTomorrow.getHabitTitle()));

        solo.clickOnText(myExampleHabitToday.getHabitTitle());

        // The correct activity should open up
        solo.assertCurrentActivity(
                "Clicking on a habit title on the home page does not open HabitDetail page",
                HabitDetailActivity.class);
        assertTrue(solo.searchButton("Edit"));
        assertTrue(solo.searchButton("Delete"));
        solo.goBack();
        solo.assertCurrentActivity(
                "Does not return to MainActivity from the Habitdetail page when cancel is clicked",
                HomeActivity.class);

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
        assertTrue(solo.searchButton("Edit"));
        assertTrue(solo.searchButton("Delete"));
    }


    @Test
    public void test6_CreateAnotherAccountAndSendRequest() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot2");
        solo.enterText((EditText) solo.getView(R.id.Password), "password2");
        solo.clickOnButton("Create Account");
        solo.clickOnButton("Yes");
        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the create account button does not navigate to HomeActivity",
                HomeActivity.class);

        solo.clickOnButton("Feed");
        solo.assertCurrentActivity("Cannot navigate to feed", FeedActivity.class);

        solo.enterText((EditText) solo.getView(R.id.SearchName), "soloRobot1");
        solo.clickOnButton("Send Request");
    }


    @Test
    public void test7_AcceptFollowRequest() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        solo.clickOnButton("Feed");
        solo.assertCurrentActivity("Cannot navigate to feed", FeedActivity.class);

        // Accept the follow request
        solo.clickOnText("soloRobot2");
        solo.clickOnButton("Accept");
    }

    @Test
    public void test8_SeeSharedHabits() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot2");
        solo.enterText((EditText) solo.getView(R.id.Password), "password2");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        solo.clickOnButton("Feed");
        solo.assertCurrentActivity("Cannot navigate to feed", FeedActivity.class);

        assertTrue(solo.searchText("soloRobot1"));

        // Click on our new following account
        solo.clickOnText("soloRobot1");
        solo.assertCurrentActivity("Cant see shared profiles", ProfileActivity.class);

        // Can see the habit added to the other profile from this profile successfully
        assertTrue(solo.searchText("solorobothabit1"));
    }


    @Test
    public void test9_DeleteHabits() {
        solo.enterText((EditText) solo.getView(R.id.username), "soloRobot1");
        solo.enterText((EditText) solo.getView(R.id.Password), "password1");

        // Click on the login button
        solo.clickOnView((Button) solo.getView(R.id.Login));

        // Check that we are on the Home page
        solo.assertCurrentActivity(
                "Clicking on the login button does not navigate to HomeActivity",
                HomeActivity.class);

        solo.clickOnButton("Profile");

        solo.assertCurrentActivity("Could not reach Profile page", ProfileActivity.class);
        solo.clickOnText(myExampleHabitTomorrow.getHabitTitle());

        // The correct activity should open up
        solo.assertCurrentActivity(
                "Clicking on a habit title on the profile page does not open HabitDetail page",
                HabitDetailActivity.class);

        // Delete the two habits we added
        solo.clickOnView((Button) solo.getView(R.id.habit_detail_delete_button));
        solo.clickOnButton("Yes");
        solo.assertCurrentActivity("Correctly deleted the habit", ProfileActivity.class);

        // Delete the other habit
        solo.clickOnText(myExampleHabitToday.getHabitTitle());
        solo.assertCurrentActivity("", HabitDetailActivity.class);
        solo.clickOnView((Button) solo.getView(R.id.habit_detail_delete_button));
        solo.clickOnButton("Yes");
    }


    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @AfterClass
    public static void onTimeTearDown() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("profiles")
                .document("soloRobot1")
                .delete();
        db
                .collection("profiles")
                .document("soloRobot2")
                .delete();

        db
                .collection("habits")
                .document("waseem*2021-09-01 12:12:12")
                .delete();
        db
                .collection("habits")
                .document("waseem*2021-09-01 10:10:10")
                .delete();
    }
}
