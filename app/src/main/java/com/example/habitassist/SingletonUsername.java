package com.example.habitassist;

/**
 * This implements a username that is used throughout the app to identify the user
 * who has logged into the application.
 */
public class SingletonUsername {
    private static SingletonUsername singleton = null;
    private String username;

    private SingletonUsername(String username) {
        this.username = username;
    }

    public synchronized static void initialize(String username) {
        singleton = new SingletonUsername(username);
    }

    public static String get()  {
        if (singleton == null) {
            throw new AssertionError("Username not initialized");
        }
        return singleton.username;
    }
}
