package com.example.habitassist;

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
