package com.welove520.galleryscaleanimationdemo

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView

/**
 * Created by Raomengyang on 18-5-30.
 * Email    : ericrao@welove-inc.com
 * Desc     :
 * Version  : 1.0
 */
class RectUtil {
    fun getViewRect(activity: Activity, view: View, ignoreStatusBar: Boolean): Rect {
        val result = Rect()
        view.getGlobalVisibleRect(result)
        if (ignoreStatusBar) {
//            result.offset(0, getStatusBarHeight(activity))
        }
        return result
    }

    fun getImageViewRealRect(imageview: ImageView, ignoreStatusBar: Boolean): Rect {
        val result = Rect()
        imageview.getGlobalVisibleRect(result)
        var realResult = Rect()

        var dwidth = imageview.drawable.intrinsicWidth
        var dheight = imageview.drawable.intrinsicHeight
//        if (result.width() > dwidth) {
//
//        } else {
        realResult.top = result.top + (result.height() - dheight) / 2
        realResult.bottom = realResult.top + dheight
        realResult.left = result.left + ((result.width() - dwidth) / 2)
        realResult.right = realResult.left + dwidth
//        }

        return realResult
    }

    public fun getViewRect(view: View): Rect {
        var viewRect = Rect()
        var isInScreen = view.getGlobalVisibleRect(viewRect)
        return viewRect
    }

    fun getStatusBarHeight(activity: Activity): Int {
        val decorViewRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(decorViewRect)
        return decorViewRect.top
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

}