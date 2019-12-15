package com.antilogics.pse.core;

import com.antilogics.plugins.AntiPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class LocalPluginRepository {
    private final String dir;
    private final Map<String, AntiPlugin> fileMap = new HashMap<>();
    private WatchService watchService;


    public LocalPluginRepository(String dir) {
        this.dir = dir;
    }


    public void start() {
        File pluginDir = new File(dir);
        var jarFiles = pluginDir.listFiles((dir1, name) -> name.endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                try {
                    startPlugin(loadPlugin(jarFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("WARN: Plugin dir " + pluginDir.getAbsolutePath() + " is empty");
        }

        new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();

                Path path = pluginDir.toPath();

                path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE);

                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            try {
                                startPlugin(loadPlugin(new File(pluginDir, event.context().toString())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            unloadPlugin(event.context().toString());
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void stop() {
        try {
            watchService.close();
            fileMap.forEach(((path, plugin) -> plugin.stop()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void list() {
        fileMap.forEach((path, antiPlugin) -> System.out.println("INFO: " + antiPlugin.getName() + " from " + path));
    }


    private AntiPlugin loadPlugin(File jarFile) throws Exception {
        var classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
        var serviceLoader = ServiceLoader.load(AntiPlugin.class, classLoader);
        if (serviceLoader.findFirst().isPresent()) {
            AntiPlugin plugin = serviceLoader.findFirst().get();
            fileMap.put(jarFile.getName(), plugin);
            return plugin;
        }
        else {
            throw new Exception("Invalid plugin");
        }
    }


    private void startPlugin(AntiPlugin plugin) {
        new Thread(plugin::start).start();
    }


    private void unloadPlugin(String jarFilename) {
        AntiPlugin plugin = fileMap.get(jarFilename);
        if (plugin != null) {
            plugin.stop();
            fileMap.remove(jarFilename);
        }
        else {
            System.out.println("ERROR: Can not find plugin for " + jarFilename);
        }
    }
}
