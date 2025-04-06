package  com.example.androidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.androidgame.Inputs.TouchEvents;
import com.example.androidgame.entities.Player;
import com.example.androidgame.entities.Skeleton;
import com.example.androidgame.enviroments.GameMap;
import com.example.androidgame.systems.CollisionSystem;
import com.example.androidgame.systems.EnemySpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private Paint redPaint = new Paint();
    private SurfaceHolder holder;
    private int screenWidth;
    private int screenHeight;
    private Random rand = new Random();
    private GameLoop gameLoop;
    private TouchEvents touchEvents;

    // Game entities
    private Player player;
    private List<Skeleton> skeletons = new ArrayList<>();

    // Game systems
    private CollisionSystem collisionSystem;
    private EnemySpawner enemySpawner;

    // Testing map
    private GameMap testMap;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);

        // Initialize systems and inputs
        gameLoop = new GameLoop(this);
        touchEvents = new TouchEvents(this);

        // Get screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Create player at the center of the screen
        player = new Player(screenWidth / 2, screenHeight / 2, screenWidth, screenHeight);

        // Initialize game systems
        collisionSystem = new CollisionSystem(gameLoop);
        enemySpawner = new EnemySpawner(screenWidth, screenHeight, skeletons);

        // Create test map
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

        // Create initial skeleton
        enemySpawner.spawnImmediately();
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        if (c != null) {
            c.drawColor(Color.BLACK);

            // Draw map
            testMap.draw(c);

            // Draw touch controls
            touchEvents.draw(c);

            // Draw player
            player.draw(c);

            // Draw all skeletons
            for (Skeleton sk : skeletons) {
                sk.draw(c);
            }

            holder.unlockCanvasAndPost(c);
        }
    }

    public void update(double delta) {
        // Update enemy spawner
        enemySpawner.update();

        // Update player
        player.update(delta);

        // Update skeletons
        for (Skeleton sk : skeletons) {
            sk.update(delta);
        }

        // Check for collisions
        collisionSystem.checkCollisions(player, skeletons);
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
        player.setMoving(true, lastTouchDiff);
    }

    public void setPlayerMoveFalse() {
        player.setMoving(false, null);
    }
}