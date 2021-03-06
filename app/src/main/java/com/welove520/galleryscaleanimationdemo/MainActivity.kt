package com.welove520.galleryscaleanimationdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val options = RequestOptions()
        options.centerCrop()


        Glide.with(this)
                .load(R.drawable.bg_car)
                .apply(options)
                .into(pv_original)
        fab.setOnClickListener { view ->
            var it = Intent(this, PreviewActivity::class.java)

            var rectUtil = RectUtil()
            var rect = rectUtil.getViewRect(this@MainActivity, pv_original, true)
            Log.e("xxxx rect==> ", rect.toString())
            it.putExtra("rect", rect)
            startActivity(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}
