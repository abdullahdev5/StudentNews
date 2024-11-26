@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.news.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import java.net.URL

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsLinkScreen(
    link: String,
    navHostController: NavHostController,
) {

    var websiteHost by rememberSaveable { mutableStateOf("") }
    var websiteUrl by rememberSaveable { mutableStateOf("") }
    var isVisible by rememberSaveable { mutableStateOf(false) }
    var websiteFavicon by rememberSaveable() { mutableStateOf<Bitmap?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    val myWebView = remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(Unit) {
        val url = URL(link)
        websiteHost = url.host.replace("www.", "").replace(".com", "")
    }

    BackHandler(canGoBack) {
        myWebView.value?.goBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // title
                        Text(
                            text = websiteHost,
                            style = TextStyle(
                                fontSize = FontSize.MEDIUM.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // url
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp)
                            )
                            Text(
                                text = websiteUrl,
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    websiteFavicon?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Website's Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .widthIn(max = 30.dp)
                                .heightIn(max = 30.dp)
                                .padding(all = 5.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) DarkColor else White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isVisible) {
                LinearProgressIndicator(
                    color = Green,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        myWebView.value = this
                        webViewClient = object :
                            WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?,
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isVisible = false
                                websiteUrl = view?.originalUrl ?: ""
                                websiteFavicon = view?.favicon
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isVisible = true
                                canGoBack = view?.canGoBack() == true
                            }
                        }
                        loadUrl(link)
                    }
                }, update = { webView ->
                    webView.loadUrl(link)
                    myWebView.value = webView
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}