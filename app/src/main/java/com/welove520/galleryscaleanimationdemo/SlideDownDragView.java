package com.welove520.galleryscaleanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.Nullable;

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


    private PhotoView mPhotoView;
    private Rect zoomRect;
    private ImageView ivPreview;

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
            mPhotoView.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(300)
                    .start();
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
                if (ratio < 0.8) {
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


                                    mPhotoView.setDrawingCacheEnabled(true);//设置能否缓存图片信息（drawing cache）
                                    mPhotoView.buildDrawingCache();//如果能够缓存图片，则创建图片缓存
                                    Bitmap cacheBmp = mPhotoView.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);//如果图片已经缓存，返回一个bitmap
                                    mPhotoView.destroyDrawingCache();//释放缓存占用的资源

                                    final int dwidth = (int) (mPhotoView.getWidth() *(1 - value));
                                    final int dheight = (int) (mPhotoView.getHeight() * (1 - value));

                                    Bitmap bmp = Bitmap.createBitmap(cacheBmp, 0, 0, dwidth, dheight);
                                    ivPreview.setImageBitmap(bmp);
//                                    ivPreview.setImageBitmap(centerCrop(cacheBmp, dwidth, dheight));
//                                    mPhotoView.setImageBitmap(centerCrop(cacheBmp, dwidth, dheight));

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

    public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);

    public static Bitmap centerCrop(@NonNull Bitmap inBitmap, int width,
                                    int height) {
        if (inBitmap.getWidth() == width && inBitmap.getHeight() == height) {
            return inBitmap;
        }
        // From ImageView/Bitmap.createScaledBitmap.
        final float scale;
        final float dx;
        final float dy;
        Matrix m = new Matrix();
        if (inBitmap.getWidth() * height > width * inBitmap.getHeight()) {
            scale = (float) height / (float) inBitmap.getHeight();
            dx = (width - inBitmap.getWidth() * scale) * 0.5f;
            dy = 0;
        } else {
            scale = (float) width / (float) inBitmap.getWidth();
            dx = 0;
            dy = (height - inBitmap.getHeight() * scale) * 0.5f;
        }

        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        Bitmap result = Bitmap.createBitmap(inBitmap, 0, 0, width, height);
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(inBitmap, result);

        applyMatrix(inBitmap, result, m);
        return result;
    }

    private static void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap,
                                    Matrix matrix) {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
        clear(canvas);
    }

    // Avoids warnings in M+.
    private static void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }

    @NonNull
    private static Bitmap.Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    public void setZoomRect(Rect zoomRect) {
        this.zoomRect = zoomRect;
    }


    private OnStatusChangeListener mOnStatusChangeListener;

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        this.mOnStatusChangeListener = onStatusChangeListener;
    }

    public void setImageView(@Nullable ImageView iv_preview) {
        this.ivPreview = iv_preview;
    }

    public interface OnStatusChangeListener {
        void onFinished();

        void onStart();
    }
}
