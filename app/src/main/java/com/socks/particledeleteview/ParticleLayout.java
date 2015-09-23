package com.socks.particledeleteview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.plattysoft.leonids.ParticleSystem;

import java.lang.reflect.Field;

/**
 * Created by zhaokaiqiang on 15/9/19.
 */
public class ParticleLayout extends FrameLayout {

    private static final String TAG = "ParticleLayout";

    private ViewGroup backLayout;
    private ViewGroup frontLayout;

    private boolean isSwape = false;
    private boolean isDelete = false;


    private int animStartY;
    private int animEndY;
    private int[] location;
    private float startX;

    private Rect backLayoutRect;
    private FrameLayout.LayoutParams mLayoutParams;

    private DeleteListener mDeleteListener;
    private ParticleSystem particleSystem;

    public ParticleLayout(Context context) {
        this(context, null);
    }

    public ParticleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParticleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        location = new int[2];
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getChildCount() != 2) {
            throw new IllegalStateException("You only need two ViewGroup !");
        }

        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException(
                    "The  children in ParticleLayout must be an instance of ViewGroup");
        }

        backLayout = (ViewGroup) getChildAt(0);
        frontLayout = (ViewGroup) getChildAt(1);

        MarginLayoutParams layoutParams = (MarginLayoutParams) backLayout.getLayoutParams();

        int[] backLocation = new int[2];
        backLayout.getLocationInWindow(backLocation);
        backLayoutRect = new Rect(backLocation[0] + layoutParams.leftMargin,
                backLocation[1] + layoutParams.topMargin,
                backLocation[0] + backLayout.getMeasuredWidth() - layoutParams.rightMargin,
                backLocation[1] + backLayout.getMeasuredHeight() - layoutParams.bottomMargin);

        int[] frontLocation = new int[2];
        frontLayout.getLocationInWindow(frontLocation);
        animStartY = frontLocation[1] + layoutParams.topMargin - getStatuBarHeight();
        animEndY = frontLocation[1] + frontLayout.getHeight() - layoutParams.bottomMargin -
                getStatuBarHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > backLayoutRect.width() * 4 / 5) {
                    isSwape = true;
                    startX = event.getX();
                    particleSystem = new ParticleSystem((Activity) getContext(), 300, R.drawable.ic_partical, 1000);
                    particleSystem.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 300);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float width = startX - event.getX();
                if (isSwape && width > 0) {
                    mLayoutParams = new FrameLayout.LayoutParams((int) width, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
                    frontLayout.setLayoutParams(mLayoutParams);
                    frontLayout.getLocationInWindow(location);
                    particleSystem.updateEmitVerticalLine(frontLayout.getLeft(), animStartY, animEndY);
                } else {
                    particleSystem.stopEmitting();
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSwape = false;
                particleSystem.stopEmitting();
                getParent().requestDisallowInterceptTouchEvent(false);

                if (event.getX() >= getWidth() / 2) {
                    isDelete = false;
                } else {
                    isDelete = true;
                }

                if (isDelete) {
                    if (mDeleteListener != null) {
                        mDeleteListener.onDelete();
                    }
                    Log.d(TAG, "isDelete = " + isDelete);
                }

                mLayoutParams = new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
                frontLayout.setLayoutParams(mLayoutParams);
                break;
        }

        if (isSwape) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    public interface DeleteListener {
        void onDelete();
    }

    public void setDeleteListener(DeleteListener listener) {
        mDeleteListener = listener;
    }


    private int getStatuBarHeight() {

        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
