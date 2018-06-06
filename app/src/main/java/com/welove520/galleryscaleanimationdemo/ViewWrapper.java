package com.welove520.galleryscaleanimationdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Raomengyang on 18-6-5.
 * Email    : ericrao@welove-inc.com
 * Desc     :
 * Version  : 1.0
 */

public class ViewWrapper<T> extends View {

    private T innerView;


    public ViewWrapper(Context context) {
        super(context);
    }

    public ViewWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ViewWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setInnerView(T innerView) {
        this.innerView = innerView;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (innerView instanceof ImageView) {
            ((ImageView) innerView).setImageBitmap(bitmap);
        }
    }
}
