package com.nguyenmp.dotdotgo;

/**
 * Created by mark on 9/25/14.
 */

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.BoringLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    int score;
    private final Paint paint;
    private final int RADIUS = 125;
    private int xPos, yPos, xVel = 2, yVel = 2;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        setBackgroundColor(Color.WHITE);
        paint.setTextSize(25);
        new RedrawThread().start();
    }

    long runTime = System.currentTimeMillis();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long myTime = System.currentTimeMillis();
        long deltaTime = myTime - runTime;

        // Move
        xPos += xVel * deltaTime / 100;
        yPos += yVel * deltaTime / 100;

        // Clamp
        if (xPos < 0) xVel*=-1;
        xPos = Math.abs(xPos);
        if (xPos > getWidth() - RADIUS) {
            xPos = 2 * (getWidth() - RADIUS) - xPos;
            xVel *= - 1;
        }
        if (yPos < 0) yVel*=-1;
        yPos = Math.abs(yPos);
        if (yPos > getHeight() - RADIUS) {
            yPos = 2 * (getHeight() - RADIUS) - yPos;
            yVel *= - 1;
        }

        // Draw
        canvas.drawCircle(xPos + RADIUS / 2, yPos + RADIUS / 2, RADIUS, paint);
        canvas.drawText(Integer.toString(score), 50, 50, paint);

        runTime += deltaTime;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If the down event collides with the ball, then increase score
        // else, the user missed so we need to decrease score

        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        float scale = getContext().getResources().getDisplayMetrics().density;
        RectF rect = new RectF(xPos, yPos, xPos + (scale * RADIUS), yPos + (scale * RADIUS));
        if (rect.contains(event.getX(), event.getY())) {
            score ++;
        } else {
            score --;
            if (score < 0) score = 0;
        }
        xVel = (int) Math.copySign(score * 2 + 5, xVel);
        yVel = (int) Math.copySign(score * 2 + 5, xVel);


        return super.onTouchEvent(event);
    }

    /**
     * This thread calls invalidate on the view every 1/30th of a second
     * so we get essentially what is a 30fps game view
     */
    private final class RedrawThread extends Thread {
        @Override
        public void run() {
            while (true) {
                postInvalidate();
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}