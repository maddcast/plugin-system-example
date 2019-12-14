package com.example.plugins.http;

import com.antilogics.plugins.AntiPlugin;

public class HttpPlugin implements AntiPlugin {
    @Override
    public String getName() {
        return "HttpPlugin 1.0";
    }

    @Override
    public void start() {
        System.out.println("Received start signal");
    }

    @Override
    public void stop() {
        System.out.println("Received stop signal");
    }
}
