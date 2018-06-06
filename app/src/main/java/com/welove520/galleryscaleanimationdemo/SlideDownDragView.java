package com.welove520.galleryscaleanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionPropagation;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Raomengyang on 18-5-30.
 * Email    : ericrao@welove-inc.com
 * Desc     :
 * Version  : 1.0
 */

public class SlideDownDragView extends FrameLayout {
    private static final String TAG = SlideDownDragView.class.getSimpleName();

    float mLastX = 0;
    float mLastY = 0;
    float ratio = 0;

    private View mPhotoView;
    private Rect zoomRect;
    private ViewGroup mContainer;

    public SlideDownDragView(Context context) {
        this(context, null);
    }

    public SlideDownDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDownDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int index = 0; index < getChildCount(); index++) {
            Log.e("xxxxx ", getChildAt(index).getClass().getCanonicalName());
        }
        mPhotoView = getChildAt(0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (!isEnabled()) {
//            mPhotoView.animate()
//                    .translationX(0)
//                    .translationY(0)
//                    .scaleX(1)
//                    .scaleY(1)
//                    .setInterpolator(new OvershootInterpolator())
//                    .setDuration(300)
//                    .start();
//            return super.onInterceptTouchEvent(ev);
//        }
        int alpha = 0;
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

                alpha = (int) (255 * ratio);
                if (alpha > 255) {
                    alpha = 255;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                if (getParent() != null) {
                    if (getParent() instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) getParent();
                        viewGroup.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (ratio < 0.8 && zoomRect != null) {
                    final float originWidth = zoomRect.width() * 1.0f / getWidth();
                    final float originHeight = zoomRect.height() * 1.0f / getHeight();
                    Log.e(TAG, "originWidth ==> " + originWidth + " , originHeight==> " + originHeight);
                    Log.e(TAG, "mLastX ==> " + mLastX + " , mLastY ==> " + mLastY);
                    final int finalAlpha = alpha;
//                    mPhotoView.animate()
//                            .translationX(zoomRect.centerX() - getWidth() / 2)
//                            .translationY(zoomRect.centerY() - getHeight() / 2)
////                            .scaleX(originWidth)
////                            .scaleY(originHeight)
//                            .setInterpolator(new AccelerateDecelerateInterpolator())
//                            .setDuration(300)
//                            .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                    Log.e(TAG, "animation.getValues()==> " + valueAnimator.getAnimatedValue());
//                                    float value = (float) valueAnimator.getAnimatedValue();
//                                    int alp = (int) (finalAlpha * (1 - value));
//                                    setBackgroundColor(Color.argb(alp, 0, 0, 0));
//                                }
//                            })
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    if (mOnStatusChangeListener != null) {
//                                        mOnStatusChangeListener.onFinished();
//                                    }
//                                }
//                            }).start();

                    TransitionManager.beginDelayedTransition(SlideDownDragView.this, new TransitionSet()
                            .addTransition(new ChangeImageTransform())
                            .addTransition(new ChangeBounds())
                    );
                    if (mPhotoView instanceof ImageView) {
                        ((ImageView) mPhotoView).setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    FrameLayout.LayoutParams lp = (LayoutParams) mPhotoView.getLayoutParams();
                    lp.width = (int) (zoomRect.width() / ratio);
                    lp.height = (int) (zoomRect.height() / ratio);
                    lp.leftMargin = zoomRect.left;
                    lp.topMargin = zoomRect.top;
                    lp.rightMargin = zoomRect.right;
                    lp.bottomMargin = zoomRect.bottom;
                    mPhotoView.setLayoutParams(lp);

                    return true;
//                            .start();
                } else {
                    setBackgroundColor(Color.argb(255, 0, 0, 0));
                    if (getParent() != null) {
                        if (getParent() instanceof ViewGroup) {
                            ViewGroup viewGroup = (ViewGroup) getParent();
                            viewGroup.setBackgroundColor(Color.argb(255, 0, 0, 0));
                        }
                    }
                    mPhotoView.animate()
                            .translationX(0)
                            .translationY(0)
                            .scaleX(1)
                            .scaleY(1)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(250)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    setBackgroundColor(Color.argb(0, 0, 0, 0));
                                }
                            })
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

    public void setParentView(@Nullable RelativeLayout rl_container) {
        mContainer = rl_container;
    }

    public interface OnStatusChangeListener {
        void onFinished();
    }
}
