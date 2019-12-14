package com.antilogics.pse;

import com.antilogics.pse.core.LocalPluginRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException {
        var pluginRepository = new LocalPluginRepository("plugins");

        pluginRepository.start();

        var consoleIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            switch (consoleIn.readLine().toLowerCase()) {
                case "exit":
                case "quit":
                    pluginRepository.stop();
                    System.exit(0);
                case "list":
                    pluginRepository.list();
                    break;
            }
        }
    }
}
