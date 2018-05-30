package com.welove520.galleryscaleanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

/**
 * Created by Raomengyang on 18-5-30.
 * Email    : ericrao@welove-inc.com
 * Desc     :
 * Version  : 1.0
 */

public class SlideDownDragView extends FrameLayout {
    private static final String TAG = SlideDownDragView.class.getSimpleName();
    private static final int FREE_MODE = -1;
    private static final int EXIT_MODE = 1;
    private int mCurrentMode;


    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;


    private View mPhotoView;
    private Rect zoomRect;

    public SlideDownDragView(Context context) {
        this(context, null);
    }

    public SlideDownDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDownDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideDownDragView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        mCurrentMode = FREE_MODE;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int index = 0; index < getChildCount(); index++) {
            Log.e("xxxxx ", getChildAt(index).getClass().getCanonicalName());
        }
        mPhotoView = getChildAt(0);
    }

    float mLastX = 0;
    float mLastY = 0;
    float ratio = 0;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mLastX;
                float dy = ev.getY() - mLastY;
                mLastX = ev.getX();
                mLastY = ev.getY();

                Log.e(TAG, "dx==> " + dx + " , dy==> " + dy);
                mPhotoView.setTranslationX(mPhotoView.getTranslationX() + dx);
                mPhotoView.setTranslationY(mPhotoView.getTranslationY() + dy);

                Log.e(TAG, "getTranslationX==> " + mPhotoView.getTranslationX() + "getTranslationY==> " + mPhotoView.getTranslationY());
                float scale = mPhotoView.getTranslationY() / getHeight();
                ratio = 1 - scale;
                if (ratio > 1) {
                    ratio = 1;
                } else if (ratio < 0) {
                    ratio = 0;
                }
                Log.e(TAG, "scale==> " + scale + " , ratio==> " + ratio);

                mPhotoView.setScaleX(ratio);
                mPhotoView.setScaleY(ratio);

                int alpha = (int) (255 * ratio);
                if (alpha > 255) {
                    alpha = 255;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                setBackgroundColor(Color.argb(alpha, 0, 0, 0));

                break;

            case MotionEvent.ACTION_UP:
                if (ratio < 0.7) {
                    float originWidth = zoomRect.width() * 1.0f / getWidth();
                    float originHeight = zoomRect.height() * 1.0f / getHeight();
                    Log.e(TAG, "originWidth ==> " + originWidth + " , originHeight==> " + originHeight);
                    Log.e(TAG, "mLastX ==> " + mLastX + " , mLastY ==> " + mLastY);
                    mPhotoView.animate()
                            .translationX(zoomRect.centerX() - getWidth() / 2)
                            .translationY(zoomRect.centerY() - getHeight() / 2 - zoomRect.height())
                            .scaleX(originWidth)
                            .scaleY(originHeight)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(300)
                            .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    Log.e(TAG, "animation.getValues()==> " + animation.getValues());
                                }
                            })
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (mOnStatusChangeListener != null) {
                                        mOnStatusChangeListener.onFinished();
                                    }
                                }
                            })
                            .start();
                } else {
                    mPhotoView.animate()
                            .translationX(0)
                            .translationY(0)
                            .scaleX(1)
                            .scaleY(1)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(300)
                            .start();
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }


    public void setZoomRect(Rect zoomRect) {
        this.zoomRect = zoomRect;
    }


    private OnStatusChangeListener mOnStatusChangeListener;

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        this.mOnStatusChangeListener = onStatusChangeListener;
    }

    public interface OnStatusChangeListener {
        void onFinished();

        void onStart();
    }
}
