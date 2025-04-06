package com.example.androidgame.systems;

import android.graphics.PointF;

import com.example.androidgame.Helpers.GameConstants;
import com.example.androidgame.entities.Skeleton;

import java.util.List;
import java.util.Random;

public class EnemySpawner {
    private int screenWidth;
    private int screenHeight;
    private Random rand;
    private long lastSpawnTime;
    private long spawnInterval;
    private List<Skeleton> skeletons;

    public EnemySpawner(int screenWidth, int screenHeight, List<Skeleton> skeletons) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.skeletons = skeletons;
        this.rand = new Random();
        this.lastSpawnTime = System.currentTimeMillis();
        this.spawnInterval = 5000; // Default: 5 seconds between spawns
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        // Check if it's time to spawn a new enemy
        if (currentTime - lastSpawnTime >= spawnInterval) {
            spawnSkeleton();
            lastSpawnTime = currentTime;
        }
    }

    private void spawnSkeleton() {
        // Create a random position for the new skeleton
        PointF position = new PointF(
                rand.nextInt(screenWidth),
                rand.nextInt(screenHeight)
        );

        // Assign a random direction
        int direction = rand.nextInt(4);

        // Create and add the new skeleton
        skeletons.add(new Skeleton(position, direction, screenWidth, screenHeight));
    }

    // Methods to control spawning behavior
    public void setSpawnInterval(long milliseconds) {
        this.spawnInterval = milliseconds;
    }

    public void spawnImmediately() {
        spawnSkeleton();
        lastSpawnTime = System.currentTimeMillis();
    }
}