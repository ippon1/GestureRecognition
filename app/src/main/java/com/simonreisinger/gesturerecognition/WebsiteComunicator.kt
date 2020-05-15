package com.simonreisinger.gesturerecognition

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
}