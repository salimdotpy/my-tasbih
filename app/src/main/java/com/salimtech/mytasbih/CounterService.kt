package com.salimtech.mytasbih

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast

class CounterService : Service() {

    inner class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun Close(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(mContext, HideService::class.java)
            mContext.startService(intent)
            stopSelf()
        }

        @JavascriptInterface
        fun getDs(id: Int): String {
            return getD(id)
        }

        @JavascriptInterface
        fun editDs(id: Int, name: String): Boolean {
            return editD(id, name)
        }

        @JavascriptInterface
        fun delDs(id: Int): Boolean {
            return delD(id)
        }
    }

    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var view: LinearLayout
    private lateinit var web: WebView
    private val webUrl = "file:///android_asset/counter.html"
    private lateinit var db: DBHelper

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        db = DBHelper(this)
        view = LinearLayout(this)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        web = WebView(this)
        web.layoutParams = LinearLayout.LayoutParams(440, LinearLayout.LayoutParams.WRAP_CONTENT)
        val myWebSettings: WebSettings = web.settings
        myWebSettings.javaScriptEnabled = true
        web.webViewClient = WebViewClient()
        web.addJavascriptInterface(WebAppInterface(this), "def")
        web.settings.loadsImagesAutomatically = true
        web.loadUrl(webUrl)
        web.setBackgroundColor(0x00000000)
        web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        view.addView(web)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        params.x = 0
        params.y = 0

        web.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return false
                    }
                    MotionEvent.ACTION_UP -> return false
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(view, params)
                        return true
                    }
                }
                return true
            }
        })

        windowManager.addView(view, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (view != null)
            windowManager.removeView(view)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getD(id: Int): String {
        return db.getData(id)
    }

    private fun editD(id: Int, name: String): Boolean {
        return db.updateData(id, name)
    }

    private fun delD(id: Int): Boolean {
        return db.deleteData(id)
    }
}