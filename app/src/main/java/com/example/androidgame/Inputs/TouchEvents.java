package com.example.androidgame.Inputs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.example.androidgame.GamePanel;

public class TouchEvents {
    private GamePanel gamePanel;
    private float xCenter, yCenter, radius = 150;
    private Paint circlePaint, yellowPaint;
    private float xTouch, yTouch;
    private boolean touchDown;
    private int screenWidth, screenHeight;
    private final float MARGIN = 50; // Margin from screen edges

    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        // Get screen dimensions from GamePanel
        this.screenWidth = gamePanel.getWidth();
        this.screenHeight = gamePanel.getHeight();

        // Initialize joystick position (will be updated in updateJoystickPosition)
        updateJoystickPosition();

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(7);
        circlePaint.setColor(Color.RED);

        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
    }

    // Update joystick position based on screen dimensions
    public void updateJoystickPosition() {
        if (screenWidth > 0 && screenHeight > 0) {
            // Position at bottom-left with margin
            xCenter = MARGIN + radius;
            yCenter = screenHeight - MARGIN - radius;
        }
    }

    public void draw(Canvas c) {
        // Update screen dimensions if they've changed
        if (screenWidth != gamePanel.getWidth() || screenHeight != gamePanel.getHeight()) {
            screenWidth = gamePanel.getWidth();
            screenHeight = gamePanel.getHeight();
            updateJoystickPosition();
        }

        c.drawCircle(xCenter, yCenter, radius, circlePaint);

        if (touchDown) {
            c.drawLine(xCenter, yCenter, xTouch, yTouch, yellowPaint);
            c.drawLine(xCenter, yCenter, xTouch, yCenter, yellowPaint);
            c.drawLine(xTouch, yTouch, xTouch, yCenter, yellowPaint);
        }
    }

    public boolean touchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();

                float a = Math.abs(x - xCenter);
                float b = Math.abs(y - yCenter);
                float c = (float) Math.hypot(a, b);

                if (c <= radius) {
                    touchDown = true;
                    xTouch = x;
                    yTouch = y;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchDown) {
                    xTouch = event.getX();
                    yTouch = event.getY();

                    float xDiff = xTouch - xCenter;
                    float yDiff = yTouch - yCenter;

                    gamePanel.setPLayerMoveTrue(new PointF(xDiff, yDiff));
                }
                break;

            case MotionEvent.ACTION_UP:
                touchDown = false;
                gamePanel.setPlayerMoveFalse();
                break;
        }

        return true;
    }
}