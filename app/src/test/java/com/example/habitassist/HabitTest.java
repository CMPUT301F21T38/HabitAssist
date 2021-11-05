package com.example.habitassist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class HabitTest {
    private Habit createHabit() {
        return new Habit("play games", "fun", "2021-09-01", "Monday, Tuesday");
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

}