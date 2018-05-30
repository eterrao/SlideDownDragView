package com.welove520.galleryscaleanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.github.chrisbanes.photoview.PhotoView;

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


    private PhotoView mPhotoView;
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
        mPhotoView = (PhotoView) getChildAt(0);
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

                break;

            case MotionEvent.ACTION_UP:
                if (ratio < 0.7) {
                    float originWidth = zoomRect.width() * 1.0f / getWidth();
                    float originHeight = zoomRect.height() * 1.0f / getHeight();
                    Log.e(TAG, "originWidth ==> " + originWidth + " , originHeight==> " + originHeight);
                    Log.e(TAG, "mLastX ==> " + mLastX + " , mLastY ==> " + mLastY);
                    final int finalAlpha = alpha;
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
                                    Log.e(TAG, "animation.getValues()==> " + animation.getAnimatedValue());
                                    float value = (float) animation.getAnimatedValue();
                                    int alp = (int) (finalAlpha * (1 - value));
                                    setBackgroundColor(Color.argb(alp, 0, 0, 0));


                                    final int dwidth = mPhotoView.getWidth();
                                    final int dheight = mPhotoView.getHeight();

                                    final int vwidth = getWidth() - mPhotoView.getPaddingLeft() - mPhotoView.getPaddingRight();
                                    final int vheight = getHeight() - mPhotoView.getPaddingTop() - mPhotoView.getPaddingBottom();

                                    final boolean fits = (dwidth < 0 || vwidth == dwidth)
                                            && (dheight < 0 || vheight == dheight);

                                    Matrix mDrawMatrix = new Matrix();
                                    float scale;
                                    float dx = 0, dy = 0;
                                    if (dwidth * vheight > vwidth * dheight) {
                                        scale = (float) vheight / (float) dheight;
                                        dx = (vwidth - dwidth * scale) * 0.5f;
                                    } else {
                                        scale = (float) vwidth / (float) dwidth;
                                        dy = (vheight - dheight * scale) * 0.5f;
                                    }

                                    mDrawMatrix.setScale(scale, scale);
                                    mDrawMatrix.postTranslate(Math.round(dx), Math.round(dy));

//                                    Bitmap bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_long_1));
//                                    Canvas canvas = new Canvas(bmp);
//                                    canvas.concat(mDrawMatrix);
//                                    canvas.drawBitmap();
//                                    mPhotoView.setImageBitmap(bmp);
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
