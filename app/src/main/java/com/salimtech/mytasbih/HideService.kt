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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast

class HideService : Service() {

    inner class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun Close(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    private lateinit var windowManager: WindowManager
    private lateinit var hideMe: ImageView
    private lateinit var params: WindowManager.LayoutParams
    private var cnt = 0
    private var check = "true"
    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        hideMe = ImageView(this)
        hideMe.setImageResource(R.drawable.closes)

        hideMe.layoutParams = LinearLayout.LayoutParams(20, 20)

        params = WindowManager.LayoutParams(
            90,
            90,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.RIGHT
        params.x = 0
        params.y = 0

        hideMe.setOnClickListener {
            if (check == "") {
                val intent = Intent(this, CounterService::class.java)
                startService(intent)
                stopSelf()
            }
        }

        hideMe.setOnLongClickListener {
            Toast.makeText(this@HideService, "hidden, Please launch the app again to enable it", Toast.LENGTH_SHORT).show()
            stopSelf()
            true
        }

        hideMe.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0f
            private var initialTouchY: Float = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        check = ""
                        cnt = 0
                        return false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX - (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(hideMe, params)
                        check = "true"
                        cnt++
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (check == "" || cnt <= 5)
                            check = ""
                        else
                            check = "true"
                        cnt = 0
                        return false
                    }
                }
                return false
            }
        })

        windowManager.addView(hideMe, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hideMe != null)
            windowManager.removeView(hideMe)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}