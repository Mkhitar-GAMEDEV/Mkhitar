// File: com/example/androidgame/GameLoop.java
package com.example.androidgame;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;
    private volatile boolean running = false;

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    @Override
    public void run() {
        long lastFPScheck = System.currentTimeMillis();
        int fps = 0;
        long lastDelta = System.nanoTime();
        long nanoSec = 1_000_000_000;

        while (running) {
            long nowDelta = System.nanoTime();
            double delta = (nowDelta - lastDelta) / (double) nanoSec;
            gamePanel.update(delta);
            gamePanel.render();
            lastDelta = nowDelta;
            fps++;
            long now = System.currentTimeMillis();
            if (now - lastFPScheck >= 1000) {
                // Optionally log FPS here.
                fps = 0;
                lastFPScheck += 1000;
            }
        }
    }

    public void startGameLoop() {
        running = true;
        gameThread.start();
    }

    public void stopGameLoop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
