package com.example.androidgame.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.example.androidgame.Helpers.GameConstants;

public class Player {
    private PointF position;
    private int faceDir;
    private int aniIndexY;
    private int aniTick;
    private int aniSpeed;
    private boolean isMoving;
    private PointF lastTouchDiff;
    private int screenWidth;
    private int screenHeight;

    public Player(float x, float y, int screenWidth, int screenHeight) {
        this.position = new PointF(x, y);
        this.faceDir = GameConstants.FACE_DIR.DOWN;
        this.aniIndexY = 0;
        this.aniTick = 0;
        this.aniSpeed = 7;
        this.isMoving = false;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(double delta) {
        updateMovement(delta);
        updateAnimation();
    }

    private void updateMovement(double delta) {
        if (!isMoving)
            return;

        float baseSpeed = (float) delta * 400;
        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);
        double angle = Math.atan(ratio);

        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

        if (xSpeed > ySpeed) {
            if (lastTouchDiff.x > 0) {
                faceDir = GameConstants.FACE_DIR.RIGHT;
            } else {
                faceDir = GameConstants.FACE_DIR.LEFT;
            }
        } else {
            if (lastTouchDiff.y > 0) {
                faceDir = GameConstants.FACE_DIR.DOWN;
            } else {
                faceDir = GameConstants.FACE_DIR.UP;
            }
        }

        if (lastTouchDiff.x < 0)
            xSpeed *= -1;
        if (lastTouchDiff.y < 0)
            ySpeed *= -1;

        // Update the player's position
        position.x += xSpeed * baseSpeed;
        position.y += ySpeed * baseSpeed;

        // Retrieve current sprite dimensions
        Bitmap playerSprite = GameCharacters.PLAYER.getSprite(aniIndexY, faceDir);
        int spriteWidth = playerSprite.getWidth();
        int spriteHeight = playerSprite.getHeight();

        // Clamp the player's position so they don't move off-screen
        if (position.x < 0) {
            position.x = 0;
        } else if (position.x > screenWidth - spriteWidth) {
            position.x = screenWidth - spriteWidth;
        }

        if (position.y < 0) {
            position.y = 0;
        } else if (position.y > screenHeight - spriteHeight) {
            position.y = screenHeight - spriteHeight;
        }
    }

    private void updateAnimation() {
        if (isMoving) {
            aniTick++;
            if (aniTick >= aniSpeed) {
                aniTick = 0;
                aniIndexY++;
                if (aniIndexY >= 4) {
                    aniIndexY = 0;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(GameCharacters.PLAYER.getSprite(aniIndexY, faceDir), position.x, position.y, null);
    }

    public void setMoving(boolean isMoving, PointF lastTouchDiff) {
        this.isMoving = isMoving;
        if (isMoving) {
            this.lastTouchDiff = lastTouchDiff;
        } else {
            resetAnimation();
        }
    }

    private void resetAnimation() {
        aniTick = 0;
        aniIndexY = 0;
    }

    // Getters for collision detection
    public PointF getPosition() {
        return position;
    }

    public int getAniIndexY() {
        return aniIndexY;
    }

    public int getFaceDir() {
        return faceDir;
    }
}