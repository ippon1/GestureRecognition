package com.simonreisinger.gesturerecognition

import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient

class WebsiteComunicator {
    fun initWebView(webView: WebView?): WebView? {
        if (webView != null) {
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl("https://ippon1.github.io/GestureRecognition/")
            //webView.loadUrl("https://ippon1.github.io/travellog/")
        }
        return webView
    }

    fun run(webView: WebView, number: String) {
        val scriptSrc = "ax('$number');"
        webView.post(Runnable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(scriptSrc, null)
            } else {
                webView.loadUrl("javascript:$scriptSrc")
            }
        })
    }
}