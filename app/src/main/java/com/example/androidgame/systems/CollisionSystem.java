package com.example.androidgame.systems;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

import com.example.androidgame.GameLoop;
import com.example.androidgame.entities.GameCharacters;
import com.example.androidgame.entities.Player;
import com.example.androidgame.entities.Skeleton;

import java.util.List;

public class CollisionSystem {
    private float scaleFactor = 0.8f; // Use 80% of sprite size for collision bounds
    private GameLoop gameLoop;

    public CollisionSystem(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void checkCollisions(Player player, List<Skeleton> skeletons) {
        // Get player collision bounds
        RectF playerBounds = getPlayerCollisionBounds(player);

        // Check collisions with each skeleton
        for (Skeleton skeleton : skeletons) {
            RectF skeletonBounds = getSkeletonCollisionBounds(skeleton);

            // Check for intersection
            if (RectF.intersects(playerBounds, skeletonBounds)) {
                // Collision detected!
                handleCollision(player, skeleton);
                return; // Exit early if we're stopping the game on collision
            }
        }
    }

    private RectF getPlayerCollisionBounds(Player player) {
        Bitmap playerSprite = GameCharacters.PLAYER.getSprite(
                player.getAniIndexY(), player.getFaceDir());

        int playerWidth = playerSprite.getWidth();
        int playerHeight = playerSprite.getHeight();

        // Calculate adjusted dimensions
        int adjustedWidth = (int) (playerWidth * scaleFactor);
        int adjustedHeight = (int) (playerHeight * scaleFactor);

        // Calculate offsets to center the collision box
        int offsetX = (playerWidth - adjustedWidth) / 2;
        int offsetY = (playerHeight - adjustedHeight) / 2;

        // Get player position
        PointF pos = player.getPosition();

        // Return bounds as RectF
        return new RectF(
                pos.x + offsetX,
                pos.y + offsetY,
                pos.x + offsetX + adjustedWidth,
                pos.y + offsetY + adjustedHeight
        );
    }

    private RectF getSkeletonCollisionBounds(Skeleton skeleton) {
        Bitmap skeletonSprite = GameCharacters.SKELETON.getSprite(
                skeleton.getAniIndexY(), skeleton.getDir());

        int skeletonWidth = skeletonSprite.getWidth();
        int skeletonHeight = skeletonSprite.getHeight();

        // Calculate adjusted dimensions
        int adjustedWidth = (int) (skeletonWidth * scaleFactor);
        int adjustedHeight = (int) (skeletonHeight * scaleFactor);

        // Calculate offsets to center the collision box
        int offsetX = (skeletonWidth - adjustedWidth) / 2;
        int offsetY = (skeletonHeight - adjustedHeight) / 2;

        // Get skeleton position
        PointF pos = skeleton.getPos();

        // Return bounds as RectF
        return new RectF(
                pos.x + offsetX,
                pos.y + offsetY,
                pos.x + offsetX + adjustedWidth,
                pos.y + offsetY + adjustedHeight
        );
    }

    private void handleCollision(Player player, Skeleton skeleton) {
        // Handle collision (currently just stops the game)
        gameLoop.stopGameLoop();

        // You could add more sophisticated collision handling here:
        // - Player takes damage
        // - Knockback effects
        // - Sound effects
        // - Visual effects
    }
}