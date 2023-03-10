package com.github.vikkingg13.watching;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    protected static final List<WatchService> watchServices = new ArrayList<>();
    protected List<FileListener> listeners = new ArrayList<>();
    protected final File folder;

    public FileWatcher(File folder) {
        this.folder = folder;
    }

    public static List<WatchService> getWatchServices() {
        return Collections.unmodifiableList(watchServices);
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<FileListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(FileListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FileListener listener) {
        listeners.remove(listener);
    }

    public void watch() {
        if (folder.exists()) {
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(folder.getAbsolutePath());
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchServices.add(watchService);
            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException ex) {
            Thread.currentThread().interrupt();
        }
    }

    protected  boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        Thread.sleep(5000);
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());
        }
        return key.reset();
    }

    protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {
        FileEvent event = new FileEvent(file);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(event);
            }
            if (file.isDirectory()) {
                FileWatcher watcher = new FileWatcher(file);
                watcher.setListeners(listeners);
                watcher.watch();
            }
        } else if (kind == ENTRY_MODIFY) {
            for (FileListener listener : listeners) {
                listener.onModified(event);
            }
        } else if (kind == ENTRY_DELETE) {
            for (FileListener listener : listeners) {
                listener.onDeleted(event);
            }
        }
    }
}
