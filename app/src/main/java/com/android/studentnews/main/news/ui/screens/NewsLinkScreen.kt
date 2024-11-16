package com.android.studentnews.main.news.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsLinkScreen(
    link: String,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                }
            }, update = { webView ->
                webView.loadUrl(link)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}