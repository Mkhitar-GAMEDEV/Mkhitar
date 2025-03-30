package com.example.androidgame;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;
    private volatile boolean running = false; // Flag to control the game loop

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
            double timeSinceLastDelta = nowDelta - lastDelta;
            double delta = timeSinceLastDelta / nanoSec;
            gamePanel.update(delta);
            gamePanel.render();
            lastDelta = nowDelta;
            fps++;
            long now = System.currentTimeMillis();
            if (now - lastFPScheck >= 1000) {
                // System.out.println("FPS: " + fps);
                fps = 0;
                lastFPScheck += 1000;
            }
        }
    }

    public void startGameLoop() {
        running = true;
        gameThread.start();
    }

    // This method stops the game loop
    public void stopGameLoop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
