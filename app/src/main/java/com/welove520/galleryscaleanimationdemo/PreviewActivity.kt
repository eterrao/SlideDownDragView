package com.welove520.galleryscaleanimationdemo

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_preview.*
import java.text.DecimalFormat

class PreviewActivity : AppCompatActivity() {

    private lateinit var mScaleFormat: DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        mScaleFormat = DecimalFormat("#.#")

        initView()
    }

    private fun initView() {
//        Glide.with(this)
//                .load(R.drawable.img_long_1).into(pv_photo)
//        pv_photo.setOnTouchListener { view, event ->
//            Log.e("xxxx", "p1!!.action==> " + event!!.action + " , view class: " )
//            true
//        }
        var rect = intent.getParcelableExtra<Rect>("rect")
        Log.e("xxxxxx", rect.toString())
        sddv_preview.setZoomRect(rect)
        sddv_preview.setOnStatusChangeListener(object : SlideDownDragView.OnStatusChangeListener {
            override fun onStart() {
            }

            override fun onFinished() {
                finish()
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
            }
        })

        pv_photo.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
            val scale = mScaleFormat.format(pv_photo.scale.toDouble())
            Log.d("scale", scale)
            sddv_preview.isEnabled = java.lang.Float.parseFloat(scale) <= 1
        }
    }
}
