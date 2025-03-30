package com.example.androidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.androidgame.Helpers.GameConstants;
import com.example.androidgame.Inputs.TouchEvents;
import com.example.androidgame.entities.GameCharacters;
import com.example.androidgame.enviroments.GameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private Paint redPaint = new Paint();
    private SurfaceHolder holder;
    private int screenWidth;
    private int screenHeight;
    private float x, y;
    private boolean movePlayer;
    private PointF lastTouchDiff;
    private Random rand = new Random();
    private GameLoop gameLoop;
    private TouchEvents touchEvents;
    private int playerAniIndexY, playerFaceDir = GameConstants.FACE_DIR.DOWN;
    private int aniTick;
    private int aniSpeed = 15;

    // Testing map
    private GameMap testMap;

    // Timer for spawning new skeletons
    private long lastSkeletonSpawn = System.currentTimeMillis();
    private long spawnInterval = 5000; // spawn a new skeleton every 5 seconds

    // List to hold all skeletons
    private List<Skeleton> skeletons = new ArrayList<>();

    // Inner class to encapsulate a skeleton's state
    private class Skeleton {
        PointF pos;
        int dir;
        int aniIndexY;
        int aniTick;
        long lastDirChange;

        public Skeleton(PointF pos, int dir) {
            this.pos = pos;
            this.dir = dir;
            this.aniIndexY = 0;
            this.aniTick = 0;
            this.lastDirChange = System.currentTimeMillis();
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
    }

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);
        gameLoop = new GameLoop(this);

        // Get screen dimensions first
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Create an initial skeleton
        skeletons.add(new Skeleton(new PointF(rand.nextInt(screenWidth), rand.nextInt(screenHeight)), GameConstants.FACE_DIR.DOWN));

        int[][] spriteIds = {
                {178, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 268, 266, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 223, 264, 264, 265, 265, 265, 265, 265, 268, 266, 265, 265, 223, 267, 223, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 158, 161, 265, 223, 223, 264, 264, 265, 223, 223, 266, 265, 265, 265, 265, 265, 267, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 224, 227, 264, 265, 265, 265, 265, 265, 223, 268, 223, 265, 223, 267, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 265, 265, 265, 264, 265, 223, 266, 265, 223, 265, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 223, 265, 265, 265, 223, 265, 265, 265, 268, 266, 265, 265, 265, 265, 265, 223, 267, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 264, 265, 265, 265, 265, 268, 223, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 265, 223, 265, 265, 220, 221, 221, 221, 222, 223, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 265, 265, 264, 265, 265, 223, 266, 265, 265, 265, 265, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 223, 265, 265, 265, 265, 265, 265, 265, 223, 268, 223, 265, 265, 223, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 264, 264, 223, 264, 264, 268, 264, 264, 265, 265, 268, 266, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 268, 268, 265, 223, 265, 265, 265, 265, 265, 223, 266, 265, 265, 223, 265, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 268, 223, 265, 265, 265, 265, 223, 265, 265, 265, 265, 267, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 223, 265, 264, 264, 223, 265, 265, 223, 265, 268, 266, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 223, 265, 223, 265, 265, 265, 223, 266, 265, 265, 223, 265, 223, 223, 265, 265, 265, 265, 265, 265, 267, 265, 265, 265, 223},
                {178, 223, 223, 264, 223, 265, 265, 265, 223, 265, 265, 268, 266, 265, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 223, 223, 265, 265, 265, 223, 268, 223, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 223, 265, 265, 265, 265, 223, 265, 265, 265, 268, 266, 265, 265, 223, 265, 223, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223},
                {178, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 268, 266, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 265, 223}
        };


        testMap = new GameMap(spriteIds);
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        if (c != null) {
            c.drawColor(Color.BLACK);
            testMap.draw(c);
            touchEvents.draw(c);
            // Render player sprite
            c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir), x, y, null);

            // Render all skeletons
            for (Skeleton sk : skeletons) {
                sk.draw(c);
            }

            holder.unlockCanvasAndPost(c);
        }
    }

    public void update(double delta) {
        // Check if it's time to spawn a new skeleton
        long now = System.currentTimeMillis();
        if (now - lastSkeletonSpawn >= spawnInterval) {
            // Spawn new skeleton at a random location with a random direction
            PointF pos = new PointF(rand.nextInt(screenWidth), rand.nextInt(screenHeight));
            int dir = rand.nextInt(4);
            skeletons.add(new Skeleton(pos, dir));
            lastSkeletonSpawn = now;
        }

        // Update each skeleton's movement and animation
        for (Skeleton sk : skeletons) {
            sk.update(delta);
        }

        updatePlayerMove(delta);
        updateAnimation();

        // --- Collision Detection with Smaller Hitboxes ---
        Bitmap playerSprite = GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir);
        int playerWidth = playerSprite.getWidth();
        int playerHeight = playerSprite.getHeight();

        // Use 80% of the full dimensions for collision
        float scaleFactor = 0.8f;
        int adjustedPlayerWidth = (int) (playerWidth * scaleFactor);
        int adjustedPlayerHeight = (int) (playerHeight * scaleFactor);
        // Center the smaller collision box within the player's sprite
        int playerOffsetX = (playerWidth - adjustedPlayerWidth) / 2;
        int playerOffsetY = (playerHeight - adjustedPlayerHeight) / 2;

        for (Skeleton sk : skeletons) {
            Bitmap skeletonSprite = GameCharacters.SKELETON.getSprite(sk.aniIndexY, sk.dir);
            int skeletonWidth = skeletonSprite.getWidth();
            int skeletonHeight = skeletonSprite.getHeight();

            int adjustedSkeletonWidth = (int) (skeletonWidth * scaleFactor);
            int adjustedSkeletonHeight = (int) (skeletonHeight * scaleFactor);
            int skeletonOffsetX = (skeletonWidth - adjustedSkeletonWidth) / 2;
            int skeletonOffsetY = (skeletonHeight - adjustedSkeletonHeight) / 2;

            // Adjusted boundaries for the player
            float playerLeft = x + playerOffsetX;
            float playerRight = playerLeft + adjustedPlayerWidth;
            float playerTop = y + playerOffsetY;
            float playerBottom = playerTop + adjustedPlayerHeight;

            // Adjusted boundaries for the skeleton
            float skeletonLeft = sk.pos.x + skeletonOffsetX;
            float skeletonRight = skeletonLeft + adjustedSkeletonWidth;
            float skeletonTop = sk.pos.y + skeletonOffsetY;
            float skeletonBottom = skeletonTop + adjustedSkeletonHeight;

            // Check for collision using adjusted boundaries
            if (playerLeft < skeletonRight &&
                    playerRight > skeletonLeft &&
                    playerTop < skeletonBottom &&
                    playerBottom > skeletonTop) {

                // Collision detected: stop the game loop
                gameLoop.stopGameLoop();
                break;
            }
        }
    }

    private void updatePlayerMove(double delta) {
        if (!movePlayer)
            return;

        float baseSpeed = (float) delta * 300;
        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);
        double angle = Math.atan(ratio);

        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

        if (xSpeed > ySpeed) {
            if (lastTouchDiff.x > 0) {
                playerFaceDir = GameConstants.FACE_DIR.RIGHT;
            } else {
                playerFaceDir = GameConstants.FACE_DIR.LEFT;
            }
        } else {
            if (lastTouchDiff.y > 0) {
                playerFaceDir = GameConstants.FACE_DIR.DOWN;
            } else {
                playerFaceDir = GameConstants.FACE_DIR.UP;
            }
        }

        if (lastTouchDiff.x < 0)
            xSpeed *= -1;
        if (lastTouchDiff.y < 0)
            ySpeed *= -1;

        // Update the player's position
        x += xSpeed * baseSpeed;
        y += ySpeed * baseSpeed;

        // Retrieve current sprite dimensions
        Bitmap playerSprite = GameCharacters.PLAYER.getSprite(playerAniIndexY, playerFaceDir);
        int spriteWidth = playerSprite.getWidth();
        int spriteHeight = playerSprite.getHeight();

        // Clamp the player's position so they don't move off-screen
        if (x < 0) {
            x = 0;
        } else if (x > screenWidth - spriteWidth) {
            x = screenWidth - spriteWidth;
        }

        if (y < 0) {
            y = 0;
        } else if (y > screenHeight - spriteHeight) {
            y = screenHeight - spriteHeight;
        }
    }

    // Only update the player's animation when moving
    private void updateAnimation() {
        if (movePlayer) {
            aniTick++;
            if (aniTick >= aniSpeed) {
                aniTick = 0;
                playerAniIndexY++;
                if (playerAniIndexY >= 4) {
                    playerAniIndexY = 0;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchEvents.touchEvent(event);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Update screen dimensions when surface changes
        screenWidth = width;
        screenHeight = height;
    }

    @Override

    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Handle cleanup or pausing the game loop if needed
    }

    public void setPLayerMoveTrue(PointF lastTouchDiff) {
        movePlayer = true;
        this.lastTouchDiff = lastTouchDiff;
    }

    public void setPlayerMoveFalse() {
        movePlayer = false;
        resetAnimation();
    }

    private void resetAnimation() {
        aniTick = 0;
        playerAniIndexY = 0;
    }
}
