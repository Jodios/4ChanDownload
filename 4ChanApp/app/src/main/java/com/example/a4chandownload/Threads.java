package com.example.a4chandownload;

import java.util.ArrayList;

public class Threads {

    //array of threads

    private ArrayList<ThreadN> threads;

    public Threads(ArrayList<ThreadN> threads) {
        this.threads = threads;
    }

    public ArrayList<ThreadN> getThreads() {
        return threads;
    }

    public void setThreads(ArrayList<ThreadN> threads) {
        this.threads = threads;
    }


    @Override
    public String toString() {
        return "Threads{" +
                "threads=" + threads +
                '}';
    }
}
