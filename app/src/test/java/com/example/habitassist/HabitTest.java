/*
 * This file implements Unit Tests for the model class `Habit`
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class HabitTest {
    private Habit createHabit() {
        String title = "play games", reason = "fun", startDate = "2021-09-01",
                DaysToBeDone = "Monday, Tuesday", ownerUsername = "waseem",
                timeStamp = "2021-09-01 12:12:12";
        boolean isPublic = true;
        return new Habit(title, reason, startDate, DaysToBeDone, ownerUsername, isPublic, timeStamp);
    }


    @Test
    public void testTitle() {
        assertEquals("play games", createHabit().getHabitTitle());
    }

    @Test
    public void testReason() {
        assertEquals("fun", createHabit().getReason());
    }
    @Test
    public void testStartDate() {
        assertEquals("2021-09-01", createHabit().getStartDate());
    }
    @Test
    public void testDaysToBeDone() {
        assertEquals("Monday, Tuesday", createHabit().getDaysToBeDone());
    }
    @Test
    public void testOwnerUsername() {
        assertEquals("waseem", createHabit().getOwnerUsername());
    }
    @Test
    public void testIsPublic() {
        assertEquals(true, createHabit().isPublic());
    }
    @Test
    public void testTimeStamp() {
        assertEquals("2021-09-01 12:12:12", createHabit().getTimeStamp());
    }

}
