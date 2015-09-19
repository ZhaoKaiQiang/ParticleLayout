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

/**
 * Created by zhaokaiqiang on 15/9/19.
 */
public class ParticleLayout extends FrameLayout {

    private static final String TAG = "ParticleLayout";

    private ViewGroup backLayout;
    private ViewGroup frontLayout;

    private boolean isSwape = false;
    private boolean isDelete = false;
    private Rect backLayoutRect;

    private int[] location;

    private FrameLayout.LayoutParams mLayoutParams;
    private float startX;
    private DeleteListener mDeleteListener;

    private ParticleSystem particleSystem_1_5;
    private ParticleSystem particleSystem_2_5;
    private ParticleSystem particleSystem_3_5;
    private ParticleSystem particleSystem_4_5;
    private ParticleSystem particleSystem_5_5;


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

        int[] backLocation = new int[2];
        backLayout.getLocationInWindow(backLocation);
        backLayoutRect = new Rect(backLocation[0], backLocation[1], backLocation[0] + backLayout.getMeasuredWidth(), backLocation[1] + backLayout.getMeasuredHeight());
        frontLayout = (ViewGroup) getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > backLayoutRect.width() * 3 / 4) {
                    isSwape = true;
                    startX = event.getX();
                    particleSystem_1_5 = new ParticleSystem((Activity) getContext(), 40, R.drawable.ic_partical, 1000);
                    particleSystem_1_5.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 40);
                    particleSystem_2_5 = new ParticleSystem((Activity) getContext(), 40, R.drawable.ic_partical, 1000);
                    particleSystem_2_5.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 40);
                    particleSystem_3_5 = new ParticleSystem((Activity) getContext(), 40, R.drawable.ic_partical, 1000);
                    particleSystem_3_5.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 40);
                    particleSystem_4_5 = new ParticleSystem((Activity) getContext(), 40, R.drawable.ic_partical, 1000);
                    particleSystem_4_5.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 40);
                    particleSystem_5_5 = new ParticleSystem((Activity) getContext(), 40, R.drawable.ic_partical, 1000);
                    particleSystem_5_5.setAcceleration(0.00013f, 90)
                            .setSpeedByComponentsRange(0f, 0.3f, 0.05f, 0.3f)
                            .setFadeOut(200, new AccelerateInterpolator())
                            .emitWithGravity(frontLayout, Gravity.LEFT, 40);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSwape) {
                    float width = startX - event.getX();
                    mLayoutParams = new FrameLayout.LayoutParams((int) width, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
                    frontLayout.setLayoutParams(mLayoutParams);
                    frontLayout.getLocationInWindow(location);
                    particleSystem_1_5.updateEmitPoint(frontLayout.getLeft(), location[1] + frontLayout.getHeight() * 1 / 5);
                    particleSystem_2_5.updateEmitPoint(frontLayout.getLeft(), location[1] + frontLayout.getHeight() * 2 / 5);
                    particleSystem_3_5.updateEmitPoint(frontLayout.getLeft(), location[1] + frontLayout.getHeight() * 3 / 5);
                    particleSystem_4_5.updateEmitPoint(frontLayout.getLeft(), location[1] + frontLayout.getHeight() * 4 / 5);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSwape = false;
                particleSystem_1_5.stopEmitting();
                particleSystem_2_5.stopEmitting();
                particleSystem_3_5.stopEmitting();
                particleSystem_4_5.stopEmitting();
                particleSystem_5_5.stopEmitting();
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

}
