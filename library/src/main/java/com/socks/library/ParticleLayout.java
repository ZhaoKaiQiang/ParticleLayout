package com.socks.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
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

    private boolean isSwape = false;
    private boolean isDelete = false;

    private float startX;
    private int clipWidth = 0;
    int[] backLocation;

    private Rect backLayoutRect;
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
        backLayoutRect = new Rect();
        backLocation = new int[2];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backLayout = (ViewGroup) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        backLayout.getLocationInWindow(backLocation);
        backLayoutRect.set(backLocation[0], backLocation[1],
                backLocation[0] + backLayout.getMeasuredWidth(),
                backLocation[1] + backLayout.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipRect(0, 0, backLayoutRect.right - clipWidth, getHeight());
        super.dispatchDraw(canvas);
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
                            .emitWithGravity(backLayout, Gravity.RIGHT, 300);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                clipWidth = (int) (startX - event.getX());
                if (isSwape && clipWidth > 0) {
                    requestLayout();
                    particleSystem.updateEmitVerticalLine(backLayoutRect.right - clipWidth, backLayoutRect.top - getStatuBarHeight(), backLayoutRect.bottom - getStatuBarHeight());
                } else {
                    particleSystem.stopEmitting();
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startX = 0;
                clipWidth = 0;
                invalidate();
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
                }
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
