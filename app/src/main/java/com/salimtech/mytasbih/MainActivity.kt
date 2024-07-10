package com.salimtech.mytasbih

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File

class MainActivity : AppCompatActivity() {
    inner class WebAppInterface // Instantiate the interface and set the context 
    internal constructor(var mContext: Context) {
        var vas: View? = null

        // Show a toast from the web page 
        @JavascriptInterface
        fun showToast(toast: String?) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun logOut(toast: String?) {
            finishAffinity()
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun Share(toast: String?) {
            try {
                val api = applicationContext.applicationInfo
                val filePath = api.sourceDir
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "application/vnd.android.package-archive"
                intent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse(filePath)
                )
                startActivity(Intent.createChooser(intent, "Share Using"))
                Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(mContext, "File not found", Toast.LENGTH_LONG).show()
            }
        }

        @JavascriptInterface
        fun FeedW() {
            val toNumber = "+2348076738293"
            val mUri =
                Uri.parse("https://api.whatsapp.com/send?phone=$toNumber&text=@My Tasbih\n\nPlease type your message here!")
            val mIntent = Intent("android.intent.action.VIEW", mUri)
            try {
                mIntent.setPackage("com.whatsapp")
                startActivity(mIntent)
            } catch (e: Exception) {
                mIntent.setPackage("com.whatsapp.w4b")
                startActivity(mIntent)
                Toast.makeText(
                    applicationContext,
                    "Please Install Whatsapp Massenger App in your Devices",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        @JavascriptInterface
        fun FeedE() {
            val to = "ojeselimkola1999@gmail.com"
            val subject = "User's feedback"
            val message = "@My Tasbih\n\nPlease type your message here"
            val mailIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("mailto:?subject=$subject&body=$message&to=$to")
            mailIntent.data = data
            startActivity(Intent.createChooser(mailIntent, "Send mail..."))
        }

        @JavascriptInterface
        fun starts() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)){
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION)
                Toast.makeText(mContext, "Draw over other apps permission not granted", Toast.LENGTH_SHORT).show()
            } else {
                start(vas)
            }
        }

        @JavascriptInterface
        fun stops(toast: String?) {
            stop(vas)
        }

        @JavascriptInterface
        fun setDs(name: String?): Boolean {
            return setD(name)
        }

        @JavascriptInterface
        fun getDs(id: Int): String {
            return getD(id)
        }

        @JavascriptInterface
        fun editDs(id: Int, name: String?): Boolean {
            return editD(id, name)
        }

        @JavascriptInterface
        fun delDs(id: Int): Boolean {
            return delD(id)
        }
    }

    private var web: WebView? = null
    var webUrl: String? = null
    var db: DBHelper? = null
    val REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        web = findViewById<View>(R.id.web) as WebView
        webUrl = "file:///android_asset/dash.html"
        db = DBHelper(this)
        if (getD(1) == "") {
            setD("1/0")
        }
        val mywebsettings = web!!.settings
        mywebsettings.javaScriptEnabled = true
        web!!.webViewClient = WebViewClient()
        web!!.addJavascriptInterface(WebAppInterface(this), "def")

        //improve webview performance
        web!!.settings.loadsImagesAutomatically = true
        web!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        mywebsettings.domStorageEnabled = true
        mywebsettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        mywebsettings.savePassword = true
        mywebsettings.saveFormData = true
        mywebsettings.setEnableSmoothTransition(true)
        web!!.loadUrl(webUrl!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION)
        } else {
            val intent = Intent(this, CounterService::class.java)
            startService(intent)
            val i = Intent()
            i.action = Intent.ACTION_MAIN
            i.addCategory(Intent.CATEGORY_HOME)
            startActivity(i)
        }
    }

    fun start(view: View?) {
        var intent = Intent(this, CounterService::class.java)
        startService(intent)
        intent = Intent(this, HideService::class.java)
        stopService(intent)
    }

    fun stop(view: View?) {
        var intent = Intent(this, CounterService::class.java)
        stopService(intent)
        intent = Intent(this, HideService::class.java)
        stopService(intent)
    }

    fun setD(name: String?): Boolean {
        return db!!.insertData(name)
    }

    fun getD(id: Int): String {
        return db!!.getData(id)
    }

    fun editD(id: Int, name: String?): Boolean {
        return db!!.updateData(id, name)
    }

    fun delD(id: Int): Boolean {
        return db!!.deleteData(id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)){
                val intent = Intent(this, CounterService::class.java)
                startService(intent)
                val i = Intent()
                i.action = Intent.ACTION_MAIN
                i.addCategory(Intent.CATEGORY_HOME)
                startActivity(i)
            } else{
                Toast.makeText(this, "Draw over other apps permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (web!!.canGoBack()) {
            web!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
