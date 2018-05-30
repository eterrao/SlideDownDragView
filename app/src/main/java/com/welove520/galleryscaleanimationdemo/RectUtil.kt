package com.welove520.galleryscaleanimationdemo

import android.app.Activity
import android.graphics.Rect
import android.view.View

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
            result.offset(0, -getStatusBarHeight(activity))
        }
        return result
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
}