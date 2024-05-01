package com.assignment.empower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient

class WebView : AppCompatActivity() {
    private lateinit var webview: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide(Gravity.RIGHT)
        setContentView(R.layout.activity_web_view)
        webview = findViewById(R.id.webview)
        webview.webViewClient = WebViewClient()
        webview.loadUrl("https://66226fe99108f810b6175aa5--inquisitive-gingersnap-2f1f13.netlify.app/")
        webview.settings.javaScriptEnabled = true
        webview.settings.setSupportZoom(true)
    }
    override fun onBackPressed() {
        if(webview.canGoBack()) {
            webview.goBack()
        }
        else {
            super.onBackPressed()
        }
    }
}