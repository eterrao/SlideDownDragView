package com.welove520.galleryscaleanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * Custom ImageView that can animate ScaleType
 * Originally created by Wflei on 16/5/31.
 * Updated by Mike Miller (http://www.mikemilla.com) on 6/15/2017.
 * Original StackOverflow Post - https://stackoverflow.com/a/37539692/2415921
 */
public class AnimatedImageView extends android.support.v7.widget.AppCompatImageView {

    // Listener values;
    private ScaleType mFromScaleType, mToScaleType;
    private ValueAnimator mValueAnimator;
    private int mStartDelay = 0;
    private boolean isViewLayedOut = false;

    // Constructors
    public AnimatedImageView(Context context) {
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Set default original scale type
        mFromScaleType = getScaleType();

        // Set default scale type for animation
        mToScaleType = getScaleType();

        // Init value animator
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);

        // Set resource
        updateScaleType(mFromScaleType, false);
    }

    /**
     * Sets the scale type we want to animate to
     *
     * @param toScaleType
     */
    public void setAnimatedScaleType(ScaleType toScaleType) {
        mToScaleType = toScaleType;
    }

    /**
     * Duration of the animation
     *
     * @param animationDuration
     */
    public void setAnimDuration(int animationDuration) {
        mValueAnimator.setDuration(animationDuration);
    }

    /**
     * Set the time delayed for the animation
     *
     * @param startDelay The delay (in milliseconds) before the animation
     */
    public void setStartDelay(Integer startDelay) {
        mStartDelay = startDelay == null ? 0 : startDelay;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        mFromScaleType = scaleType;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateScaleType(mFromScaleType, false);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        updateScaleType(mFromScaleType, false);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateScaleType(mFromScaleType, false);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        updateScaleType(mFromScaleType, false);
    }

    /**
     * Animates the current view
     * and updates it's current asset
     */
    public void startAnimation() {

        // This will run the animation with a delay (delay is defaulted at 0)
//        postDelayed(startDelay, mStartDelay);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // View has been laid out
        isViewLayedOut = true;

        // Animate change when bounds are official
        if (changed) {
            updateScaleType(mToScaleType, false);
        }
    }

    /**
     * animate to scaleType
     *
     * @param toScaleType
     */
    private void updateScaleType(final ScaleType toScaleType, boolean animated) {

        // Check if view is laid out
        if (!isViewLayedOut) {
            return;
        }

        // Cancel value animator if its running
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
        }

        // Set the  scale type
        super.setScaleType(mFromScaleType);

        // Get values for the current image matrix
        setFrame(getLeft(), getTop(), getRight(), getBottom());
        Matrix srcMatrix = getImageMatrix();
        final float[] srcValues = new float[9], destValues = new float[9];
        srcMatrix.getValues(srcValues);

        // Set the scale type to the new type
        super.setScaleType(toScaleType);
        setFrame(getLeft(), getTop(), getRight(), getBottom());
        Matrix destMatrix = getImageMatrix();
        if (toScaleType == ScaleType.FIT_XY) {
            float sX = ((float) getWidth()) / getDrawable().getIntrinsicWidth();
            float sY = ((float) getHeight()) / getDrawable().getIntrinsicHeight();
            destMatrix.postScale(sX, sY);
        }
        destMatrix.getValues(destValues);

        // Get translation values
        final float transX = destValues[2] - srcValues[2];
        final float transY = destValues[5] - srcValues[5];
        final float scaleX = destValues[0] - srcValues[0];
        final float scaleY = destValues[4] - srcValues[4];

        // Set the scale type to a matrix
        super.setScaleType(ScaleType.MATRIX);

        // Listen to value animator changes
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                float[] currValues = Arrays.copyOf(srcValues, srcValues.length);
                currValues[2] = srcValues[2] + transX * value;
                currValues[5] = srcValues[5] + transY * value;
                currValues[0] = srcValues[0] + scaleX * value;
                currValues[4] = srcValues[4] + scaleY * value;
                Matrix matrix = new Matrix();
                matrix.setValues(currValues);
                setImageMatrix(matrix);
            }
        });

        // Save the newly set scale type after animation completes
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // Set the from scale type to the newly used scale type
                mFromScaleType = toScaleType;
            }
        });

        // Start the animation
        if (animated) {
            mValueAnimator.start();
        }
    }
}