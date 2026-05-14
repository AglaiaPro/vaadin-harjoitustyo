package com.example.harjoitustyo.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class PushBroadcaster {

    private static final List<Consumer<String>> LISTENERS = new CopyOnWriteArrayList<>();

    private PushBroadcaster() {
    }

    public static Registration register(Consumer<String> listener) {
        LISTENERS.add(listener);
        return () -> LISTENERS.remove(listener);
    }

    public static void broadcast(String message) {
        LISTENERS.forEach(listener -> listener.accept(message));
    }

    public interface Registration {
        void remove();
    }
}
