package com.ridebooking.util;

public class SceneManager {
    private static final SceneManager instance = new SceneManager();

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        return instance;
    }

    public void switchScene(String name) {
        // No-op in Swing-based implementation
    }
}
