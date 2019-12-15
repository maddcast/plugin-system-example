package com.example.plugins.http;

import com.antilogics.plugins.AntiPlugin;

public class HttpPlugin implements AntiPlugin {
    private boolean running;


    @Override
    public String getName() {
        return "HttpPlugin 1.0";
    }

    @Override
    public void start() {
        System.out.println("Received start signal");
        running = true;
        try {
            while (running) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Received stop signal");
        running = false;
    }
}
