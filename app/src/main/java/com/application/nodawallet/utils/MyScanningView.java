package com.application.nodawallet.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MyScanningView extends View {
    private Paint paint = new Paint();
    private int mPosY = 0;
    private boolean runAnimation = true;
    private boolean showLine = true;
    private Handler handler;
    private Runnable refreshRunnable;
    private boolean isGoingDown = true;
    private int mHeight;

    public MyScanningView(Context context) {
        super(context);
        init();
    }

    public MyScanningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyScanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5.0f);
        //Add anything else you want to customize your line, like the stroke width
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {
        mHeight = canvas.getHeight();
        if (showLine) {
            canvas.drawLine(0, mPosY, canvas.getWidth(), mPosY, paint);
        }
        if (runAnimation) {
            handler.postDelayed(refreshRunnable, 0);
        }
    }

    public void startAnimation() {
        runAnimation = true;
        showLine = true;
        this.invalidate();
    }

    public void stopAnimation() {
        runAnimation = false;
        showLine = false;
        reset();
        this.invalidate();
    }

    private void reset() {
        mPosY = 0;
        isGoingDown = true;
    }

    private void refreshView() {
        //Update new position of the line
        if (isGoingDown) {
            mPosY += 5;
            if (mPosY > mHeight) {
                mPosY = mHeight;
                isGoingDown = false;
            }
        } else {
            //We invert the direction of the animation
            mPosY -= 5;
            if (mPosY < 0) {
                mPosY = 0;
                isGoingDown = true;
            }
        }
        this.invalidate();
    }
}