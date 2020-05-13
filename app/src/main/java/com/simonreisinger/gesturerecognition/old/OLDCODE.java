package com.simonreisinger.gesturerecognition.old;/*
package com.simonreisinger.gesturerecognition

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
        var webView: WebView? = null
        var imageView: ImageView? = null;

// @Override
@SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            webView = findViewById<View>(R.id.webview) as WebView
        webView!!.webViewClient = WebViewClient()
        webView!!.loadUrl("https://ippon1.github.io/travellog/")
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        val buttonClick =
        findViewById<View>(R.id.playButton) as Button
        buttonClick.setOnClickListener {
        buttonClick.setBackgroundColor(Color.BLACK)
        run("ax();")
        }
        ////////////////////////////////////////////////////////////
        //val imgProcessing = ImageProcessing()
        //imgProcessing.loadImageFromFile(this)
        //print(imageProcessing.name)


        }

        fun run(scriptSrc: String) {
        webView!!.post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        webView!!.evaluateJavascript(scriptSrc, null)
        } else {
        webView!!.loadUrl("javascript:$scriptSrc")
        }
        }
        }

        // @Override
        override fun onBackPressed() {
        if (webView!!.canGoBack()) {
        webView!!.goBack()
        } else {
        super.onBackPressed()
        }
        }


        }
        */
