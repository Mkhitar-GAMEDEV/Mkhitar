package com.example.androidgame.entities;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.example.androidgame.Helpers.GameConstants;

import java.util.Random;

public class Skeleton {
    private PointF pos;
    private int dir;
    private int aniIndexY;
    private int aniTick;
    private long lastDirChange;
    private int aniSpeed = 7;
    private Random rand = new Random();
    private int screenWidth;
    private int screenHeight;

    public Skeleton(PointF pos, int dir, int screenWidth, int screenHeight) {
        this.pos = pos;
        this.dir = dir;
        this.aniIndexY = 0;
        this.aniTick = 0;
        this.lastDirChange = System.currentTimeMillis();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(double delta) {
        long now = System.currentTimeMillis();
        // Change direction every 2 seconds
        if (now - lastDirChange >= 2000) {
            dir = rand.nextInt(4);
            lastDirChange = now;
        }

        // Update position based on current direction
        switch (dir) {
            case GameConstants.FACE_DIR.DOWN:
                pos.y += delta * 300;
                if (pos.y >= screenHeight)
                    dir = GameConstants.FACE_DIR.UP;
                break;
            case GameConstants.FACE_DIR.UP:
                pos.y -= delta * 300;
                if (pos.y <= 0)
                    dir = GameConstants.FACE_DIR.DOWN;
                break;
            case GameConstants.FACE_DIR.LEFT:
                pos.x -= delta * 300;
                if (pos.x <= 0)
                    dir = GameConstants.FACE_DIR.RIGHT;
                break;
            case GameConstants.FACE_DIR.RIGHT:
                pos.x += delta * 300;
                if (pos.x >= screenWidth)
                    dir = GameConstants.FACE_DIR.LEFT;
                break;
        }

        // Update skeleton animation (always animating)
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndexY++;
            if (aniIndexY >= 4) {
                aniIndexY = 0;
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(GameCharacters.SKELETON.getSprite(aniIndexY, dir), pos.x, pos.y, null);
    }

    // Getters for collision detection
    public PointF getPos() {
        return pos;
    }

    public int getAniIndexY() {
        return aniIndexY;
    }

    public int getDir() {
        return dir;
    }
}