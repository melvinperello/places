package com.melvinperello.places

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.melvinperello.places.util.ToastAdapter

class PlaceNewActivity : AppCompatActivity() {

    private val btnSample: Button by bind(R.id.btnSample)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_new)
        supportActionBar?.setTitle("New Place")

        btnSample.setOnClickListener({
            ToastAdapter.show(applicationContext, "Hello");
        })

    }

    /**
     * some binding function found in the internet. ?
     * <p>
     * source: https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
     */
    fun <T : View> AppCompatActivity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
    }

}
