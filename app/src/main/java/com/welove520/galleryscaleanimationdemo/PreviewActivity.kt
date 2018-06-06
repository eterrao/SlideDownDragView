package com.welove520.galleryscaleanimationdemo

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_preview.*
import java.text.DecimalFormat

class PreviewActivity : AppCompatActivity() {

    private lateinit var mScaleFormat: DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        mScaleFormat = DecimalFormat("#.#")

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView()
    }

    private fun initView() {
        val options = RequestOptions();
        options.fitCenter()
        Glide.with(this)
                .load(R.drawable.bg_car)
                .apply(options)
                .into(pv_photo)
        var rect = intent.getParcelableExtra<Rect>("rect")
        Log.e("xxxxxx", rect.toString())
        sddv_preview.setZoomRect(rect)
//        sddv_preview.setImageView(iv_preview)
        sddv_preview.setOnStatusChangeListener {
            finish()
            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
        }
        sddv_preview.setParentView(rl_container)

        pv_photo.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
            val scale = mScaleFormat.format(pv_photo.scale.toDouble())
            Log.d("scale", scale)
            sddv_preview.isEnabled = java.lang.Float.parseFloat(scale) <= 1
        }
    }
}
